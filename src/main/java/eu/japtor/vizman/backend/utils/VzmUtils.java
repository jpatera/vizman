package eu.japtor.vizman.backend.utils;

import org.apache.poi.ss.formula.functions.T;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class VzmUtils {

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static boolean isAlertModifCondition(String updatedBy) {
        return updatedBy.toLowerCase().equals("vancik");
    }

}
