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

@Entity
@Table(name = "Size", schema = "dbo", catalog = "FashionShop")
public class Size {

	@Id
	@GeneratedValue
	@Column(name = "SizeID", unique = true, nullable = false)
	private int sizeId;
	
	@Column(name = "SizeName", nullable = false)
	private String sizeName;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "size")
	private Set<OrderDetail> orderDetail = new HashSet<>(0);
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "size")
	private Set<Variation> variation = new HashSet<>(0);	

	public Size(int sizeId, String sizeName){
		super();
		this.sizeId = sizeId;
		this.sizeName = sizeName;
	}

	public Size() {
		super();
	}

	public int getSizeId() {
		return sizeId;
	}

	public void setSizeId(int sizeId) {
		this.sizeId = sizeId;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}
	
	
}
