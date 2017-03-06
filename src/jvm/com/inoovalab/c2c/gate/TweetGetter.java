package com.inoovalab.c2c.gate;


import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by root on 2/26/17.
 */
public class TweetGetter {






    public static void main(String[] args) {
        final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);

        String[]a={"7uI6WrXmzSicSRLn0ZwmlqhvV","VTdvLZm0zceuTfrFTao7ZiWcjTAEp99AVJlkTTqi2IF8aW8oU2","742561090786099200-EsCNStEd4tjBi4tVshUmM4ZuEtfVGpg","adL7N4YJv99eMexEmP7EePVo00Xyhl8Vib0lvlpiavF39"};
        String consumerKey = a[0].toString();
        String consumerSecret = a[1].toString();
        String accessToken = a[2].toString();
        String accessTokenSecret = a[3].toString();
        String topicName = args[4].toString();
        String[] keyWords = {"java","phython"};
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                queue.offer(status);

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {
                // System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        FilterQuery query = new FilterQuery().track(keyWords);
        twitterStream.filter(query);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int i = 0;
        while (i<10){
            Status ret = queue.poll();

            if (ret == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }else {
                for(HashtagEntity hashtage : ret.getHashtagEntities()) {
                    System.out.println("Hashtag: " + hashtage.getText());
                }
            }
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        twitterStream.shutdown();
    }
}
