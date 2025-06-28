package tad.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import tad.Memento.CategoryMemento;

@Entity
@Table(name = "Category", schema = "dbo", catalog = "FashionShop")
public class Category {

	@Id
	@GeneratedValue
	@Column(name = "CategoryID", unique = true, nullable = false)
	private int categoryId;

	@Column(name = "Name", nullable = false)
	private String name;

	@Column(name = "Image")
	private String image;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	private Set<Product> products = new HashSet<>(0);

	public Category() {
	}

	public Category(int categoryId, String name, String image) {
		this.categoryId = categoryId;
		this.name = name;
		this.image = image;
	}

	public Category(int categoryId, Account account, String name, Set<Product> products) {
		this.categoryId = categoryId;
		this.name = name;
		this.products = products;
	}

	public int getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Product> getProducts() {
		return this.products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	
	/**
     * Tạo snapshot (Memento) từ trạng thái hiện tại (chỉ name & image).
     */
    public CategoryMemento saveToMemento() {
        return new CategoryMemento(this.categoryId, this.name, this.image);
    }

    /**
     * Nếu cần khôi phục lại trạng thái cũ (undo), ta gọi phương thức này.
     */
    public void restoreFromMemento(CategoryMemento memento) {
    	if (memento == null) {
            throw new IllegalArgumentException("Memento cannot be null");
        }
        
        // Chỉ khôi phục các trường được phép
        this.categoryId = memento.getId(); // Quan trọng: khôi phục cả ID
        this.name = memento.getName();
        this.image = memento.getImage(); 
    }
}
