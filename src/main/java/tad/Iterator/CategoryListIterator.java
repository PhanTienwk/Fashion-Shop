package tad.Iterator;

import java.util.ArrayList;
import java.util.List;

import tad.entity.Category;

public class CategoryListIterator implements CategoryIterator{
	private final List<Category> categoryList;
    private int position = 0;
    
    public CategoryListIterator(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
    
    @Override
	public boolean hasNext() {
		return position < categoryList.size();
	}

	@Override
	public Category next() {
		if (!hasNext()) {
            throw new java.util.NoSuchElementException("Khong co phan tu de duyet");
        }
        return categoryList.get(position++);
	}
}
