package tad.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Variation", schema = "dbo", catalog = "FashionShop")
public class Variation {
	
	@Id
	@GeneratedValue
	@Column(name = "VariationID", unique = true, nullable = false)
	private int variationId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ProductID")
	private Product product;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SizeID")
	private Size size;
	
	@Column(name = "Quantity")
	private int quantity;

	public Variation(int variationId, Product product, Size size, int quantity) {
		super();
		this.variationId = variationId;
		this.product = product;
		this.size = size;
		this.quantity = quantity;
	}

	public Variation() {
		super();
	}

	public int getVariationId() {
		return variationId;
	}

	public void setVariationId(int variationId) {
		this.variationId = variationId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
