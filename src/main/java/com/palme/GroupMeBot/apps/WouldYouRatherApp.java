package com.palme.GroupMeBot.apps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jersey.repackaged.com.google.common.base.Preconditions;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.palme.GroupMeBot.groupme.client.GroupMeClient;

public class WouldYouRatherApp implements Runnable {
//    private static final Interval BUSINESS_HOURS = new Interval
    private final GroupMeClient groupMeClient;
    private final List<String> fullQuestionsToAsk;
    private final List<String> newItemsToAsk;
    private final Map<Integer, String> alreadyAskedItems;
    private final File askedQuestionFile;
    private final boolean askBasedOnInterval;   //Configuration to determine whether we ask on a fixed interval or we ask only after x many hours of dead air time
    private Instant heartbeat = new Instant();
    private final int intervalHours;

    /**
     *
     * @param groupMeClient
     * @param askHourly
     * @param hours
     *      Interval of hours to ask the question or hours of dead air time
     * @throws IOException
     * @throws URISyntaxException
     */
    public WouldYouRatherApp(final GroupMeClient groupMeClient, final boolean askHourly, final int hours)
            throws IOException, URISyntaxException {
        this.groupMeClient = Preconditions.checkNotNull(groupMeClient);
        newItemsToAsk = getStringsFromFile("input.txt");
        fullQuestionsToAsk = getStringsFromFile("questions.txt");

        this.askedQuestionFile = new File("alreadyAskedQuestions.txt");
        alreadyAskedItems = Maps.newHashMap();
        if (askedQuestionFile != null) {
            final List<String> usedItemsList = Files.readLines(this.askedQuestionFile, Charsets.UTF_8);
            System.out.println(usedItemsList);
            for (String usedItem : usedItemsList) {
                if(fullQuestionsToAsk.contains(usedItem)) {
                    fullQuestionsToAsk.remove(usedItem);
                }
                if(newItemsToAsk.contains(usedItem)) {
                    newItemsToAsk.remove(usedItem);
                }
                alreadyAskedItems.put(usedItem.hashCode(), usedItem);
            }
        }

        this.askBasedOnInterval = askHourly;
        this.intervalHours = hours;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(shouldAskQuestion()) {
                    askQuestion();
                }
                if(askBasedOnInterval) {
                    Thread.sleep(Duration.standardHours(this.intervalHours).getMillis());
                } else {
                    //Sleep for 20 seconds. and check heartbeat again
                    Thread.sleep(Duration.standardSeconds(20).getMillis());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            tearDown();
        }
    }

    private boolean shouldAskQuestion() {
        final Instant now = new Instant();
        final int hourOfDay = now.toDateTime().getHourOfDay();

//        Check if we are within "business" hours 9-8 PST
        if(hourOfDay < 9 || hourOfDay > 20) {
            return false;
        }
//
        if(askBasedOnInterval) {
            return true;
        }

        if(this.heartbeat.isAfter(now)) {   //This can happen if it gets rezzed in this very short window^
            return false;
        } else {
            final long diffTimeMillis = now.getMillis() - this.heartbeat.getMillis();
            if(Duration.millis(diffTimeMillis).getStandardHours() >= this.intervalHours) {
                System.out.println("diff time!  In seconds TRUEZ! " + Duration.millis(diffTimeMillis).toStandardSeconds());
                return true;
            }
        }

        return false;
    }

    final void askQuestion() {
        final QuestionType typeToAsk = QuestionType.getRandomQuestionType();
        switch (typeToAsk) {
        case FULL_QUESTION:
            final String question = getUnaskedItemFromList(fullQuestionsToAsk);
            groupMeClient.sendMessage(question);
            refreshHeartbeat();
            break;
        case TWO_ITEMS:
            final String item1 = getUnaskedItemFromList(newItemsToAsk);
            final String item2 = getUnaskedItemFromList(newItemsToAsk);
            groupMeClient.sendMessage(String.format(
                    "Would you rather have %s or %s?", item1, item2));
            refreshHeartbeat();
            break;
        default:
            System.out.println("FUCK");
        }
    }

    private String getUnaskedItemFromList(final List<String> itemList) {
        if (itemList.size() < 1) {
            return "Guys.. Tell Chris to restock the questions because I am fucking out.";
        }

        final Random random = new Random();
        for (int iterationsMade = 0; iterationsMade < itemList.size() * 2; iterationsMade++) {
            final int indexOfHit = random.nextInt(itemList.size());
            final String potentialItem = itemList.get(indexOfHit);
            if (alreadyAskedItems.containsKey(potentialItem.hashCode())) {
                itemList.remove(indexOfHit);
                continue;
            } else {
                itemList.remove(indexOfHit);
                saveAskedQuestions(potentialItem);
                return potentialItem;
            }
        }

        for (int index = 0; index < itemList.size(); index++) {
            final String potentialItem = itemList.get(index);
            if (alreadyAskedItems.containsKey(potentialItem.hashCode())) {
                itemList.remove(index);
                saveAskedQuestions(potentialItem);
                continue;
            } else {
                itemList.remove(index);
                return potentialItem;
            }
        }

        return "Guys.. Tell Chris to restock the questions because I am fucking out.";
    }

    private void saveAskedQuestions(final String itemToMarkAsAsked){
        alreadyAskedItems.put(itemToMarkAsAsked.hashCode(), itemToMarkAsAsked);
        try {
            Files.append(itemToMarkAsAsked +"\n", this.askedQuestionFile, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tearDown() {
        System.out.println("tear down");
    }

    private List<String> getStringsFromFile(final String fileName)
            throws URISyntaxException, IOException {
        final List<String> result = Lists.newArrayList();
        final InputStream asd = this.getClass().getResourceAsStream(fileName);

        final InputStreamReader isr = new InputStreamReader(asd);
        final List<String> tempItems =
                CharStreams.readLines(isr);
        for (String item : tempItems) {
            if (item.isEmpty()) {
                continue;
            }
            result.add(item.trim());
        }
        return result;
    }

    public void refreshHeartbeat() {
        this.heartbeat = Instant.now();
    }
}
