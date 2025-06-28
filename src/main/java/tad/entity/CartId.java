package tad.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CartId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ProductID", nullable = false)
	private int productId;

	@Column(name = "AccountID", nullable = false)
	private int accountId;
	
	@Column(name = "size", nullable = false)
    private int size;

	public CartId() {
	}

	public CartId(int productId, int accountId,int size) {
		this.productId = productId;
		this.accountId = accountId;
		   this.size = size;
	}

	public int getProductId() {
		return this.productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	   public int getSize() {
	        return size;
	    }

	    public void setSize(int size) {
	        this.size = size;
	    }

	    @Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null || getClass() != other.getClass()) {
				return false;
			}
			CartId castOther = (CartId) other;
			return this.productId == castOther.productId && 
			       this.accountId == castOther.accountId &&
			       this.size == castOther.size;  // Thêm so sánh cho size
		}

}
