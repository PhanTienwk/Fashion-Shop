package tad.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "Cart", schema = "dbo", catalog = "FashionShop")
public class Cart {
	@EmbeddedId
	private CartId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("accountId")
	@JoinColumn(name = "AccountID", nullable = false, insertable = false, updatable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("productId")
	@JoinColumn(name = "ProductID", nullable = false, insertable = false, updatable = false)
	private Product product;

	@Column(name = "Quantity", nullable = false)
	private int quantity;
	
	
	@Column(name = "size", nullable = false, insertable = false, updatable = false)
    private int size;
	
	
	public Cart() {
	}
	
	public Cart(CartId id, Account account, Product product, int quantity,int size) {
		this.id = id;
		this.account = account;
		this.product = product;
		this.quantity = quantity;
		this.size=size	;
		}

	public CartId getId() {
		return this.id;
	}

	public void setId(CartId id) {
		this.id = id;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
