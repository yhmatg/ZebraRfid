package com.zebra.rfidreader.nonghang.zhongyou.shinv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LzwDecodeUtils {
    public static String decompress(List<Integer> compressed) {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 128; i++) {
            dictionary.put(i, "" + (char) i);
        }
        int icw = compressed.get(0);
        String w = dictionary.get(icw);
        compressed.remove(0);
        StringBuilder decompressed = new StringBuilder();
        decompressed.append(w);
        for (int k : compressed) {
            String entry = dictionary.containsKey(k) ? dictionary.get(k) : w + w.charAt(0);
            dictionary.put(dictionary.size(), w + entry.charAt(0));
            decompressed.append(entry);
            w = entry;
        }
        return decompressed.toString();
    }
}