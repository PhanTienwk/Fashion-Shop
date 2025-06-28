package tad.Memento;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CategoryMementoCaretaker {
    private final Map<Integer, CategoryMemento> mementoMap = new HashMap<>();
    private Integer lastDeletedId = null;

    public void saveMemento(int categoryId, CategoryMemento memento) {
        mementoMap.put(categoryId, memento);
        lastDeletedId = categoryId;
    }

    public CategoryMemento getMemento(int categoryId) {
        return mementoMap.get(categoryId);
    }
    
    public CategoryMemento getLastDeleted() {
        if (lastDeletedId != null) {
            return mementoMap.get(lastDeletedId);
        }
        return null;
    }

    public void removeMemento(int categoryId) {
        mementoMap.remove(categoryId);
        if (lastDeletedId != null && lastDeletedId == categoryId) {
            lastDeletedId = null;
        }
    }
}
