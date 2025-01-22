package com.aahilrafiq.helpers.crawling;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class CrawlContainer {
    private final Queue<String> queue;
    private final HashSet<String> visitedSites;
    private static CrawlContainer container;

    private CrawlContainer() {
        this.queue = new LinkedList<>();
        this.visitedSites = new HashSet<>();
    }

    public static CrawlContainer getInstance() {
        if(container == null) container = new CrawlContainer();
        return container;
    }

    public synchronized void enQueue(String site) {
        queue.add(site);
    }

    public synchronized String deQueue() {
        if(queue.isEmpty()) return null;
        return queue.remove();
    }

    private synchronized boolean isSiteVisited(String site) {
        return visitedSites.contains(site);
    }

    public synchronized boolean insertIfNotPresent(String site) {
        if(isSiteVisited(site)) return false;
        visitedSites.add(site);
        return true;
    }

}
