package me.frostingly.gequests;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {

    /**
     * Formats the given string with legacy only color codes.
     * @param string the string that should be formatted
     * @return a formatted string
     */
    public static String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Formats the given array list with legacy only color codes.
     * @param input the array list that should be formatted
     * @return a formatted array list
     */
    public static List<String> formatList(List<String> input) {
        List<String> ret = new ArrayList<>();
        for (String line : input) ret.add(ChatColor.translateAlternateColorCodes('&', line));
        return ret;
    }


    /**
     * Returns a boolean depending if all values in a map equal to target.
     * @param hashMap the hashmap to iterate through and give a verdict.
     * @param target the integer that all values in the hashmap will try to be.
     * @return boolean
     */
    public static boolean isEqualMap(Map<?, ?> hashMap, Object target) {
        List<Object> objects = new ArrayList<>(hashMap.values());
        return objects.stream().allMatch(object -> object.equals(target));
    }

    /**
     * Returns a boolean depending if all values in a map equal to target.
     * @param arrayList the array list to iterate through and give a verdict.
     * @param target the integer that all values in the array list will try to be.
     * @return boolean
     */
    public static boolean isEqualList(List<?> arrayList, Object target) {
        return arrayList.stream().allMatch(num -> num.equals(target));
    }

}
