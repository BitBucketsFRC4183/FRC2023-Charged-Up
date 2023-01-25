package org.bitbuckets.lib.core;

import java.util.*;

//TODO someone write tests for this
public class IdentityDriver {

    public IdentityDriver() {
        nameCache.put(0, "root");
    }

    class Record {
        final int parent;
        final int child;

        public Record(int parent, int child) {
            this.parent = parent;
            this.child = child;
        }
    }

    final List<Record> family = new ArrayList<>();
    final Map<Integer, String> fullNameCache = new HashMap<>();
    final Map<Integer, String> nameCache = new HashMap<>();

    int currentId = 0;

    public int childProcess(int parentId, String name) {
        currentId++;

        family.add(new Record(parentId, currentId));
        nameCache.put(currentId, name);

        return currentId;
    }

    public String fullPath(int id) {
        return buildPath(id);
    }

    String buildPath(int id) {

        String possible = fullNameCache.get(id);
        if (possible != null) return possible;

        Deque<String> deque = new ArrayDeque<>();
        deque.addLast(nameCache.get(id));


        int searchId = id;
        for (; ; ) {

            Record parent = findParent(searchId);
            if (parent == null) break;
            searchId = parent.parent;

            deque.addFirst(nameCache.get(parent.parent));
        }

        //w

        StringBuilder builder = new StringBuilder();

        while (!deque.isEmpty()) {
            builder.append(deque.pop()).append("/");
        }

        String str = builder.toString();

        if (!str.endsWith("/")) {
            str += "/";
        }

        fullNameCache.put(id, str);

        return str;

    }

    Record findParent(int id) {
        for (Record record : family) {
            if (record.child == id) {
                return record; //there you go
            }
        }

        return null;
    }
}
