package org.bitbuckets.lib.core;

import java.util.*;

//This needs a major rework
public class IdentityDriver {

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


    int currentId = 0; //root id

    public int childProcess(int parentId, String name) {
        currentId++;

        nameCache.put(currentId, name);
        family.add(new Record(parentId, currentId));

        return currentId;
    }

    public String fullPath(int id) {
        return buildPath(id);
    }

    String buildPath(int id) {

        String possible = fullNameCache.get(id);
        if (possible != null) return possible;

        Deque<String> deque = new ArrayDeque<>();
        String cached = nameCache.get(id); //This is done so that root can build a name without a deque insert
        //this is terrible code.

        if (cached != null) {
            deque.addLast(cached);
        }

        int searchId = id;
        for (; ; ) {

            Record parent = findParent(searchId);
            if (parent == null) break;
            searchId = parent.parent;
            if (searchId == 0) { //TODO make this non hacky
                break;
            }

            deque.addFirst(nameCache.get(parent.parent));
        }

        //w

        StringBuilder builder = new StringBuilder();

        while (!deque.isEmpty()) {
            builder.append(deque.pop()).append("/");
        }

        String str = builder.toString();

        if (!str.endsWith("/") && builder.length() != 0) {
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
