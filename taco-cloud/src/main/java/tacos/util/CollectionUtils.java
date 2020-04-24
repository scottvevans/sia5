package tacos.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
  
  public static <T> List<T> toList(Iterable<T> iterable) {
    final List<T> list = new ArrayList<>();

    iterable.forEach(t -> list.add(t));
    
    return list;
  }

}
