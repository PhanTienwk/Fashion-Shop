package tad.Memento;

import tad.DAO.IProductDAO;
import tad.entity.Product;
import tad.entity.Variation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ProductMemento {
    private final String productName;
    private final double price;
    private final int quantity;
    private final String detail;
    private final String image;
    private final int categoryId;
    private final int discountId;
    private final Date postingDate;
    private final String unit;
    private final Set<Variation> variations;

    public ProductMemento(Product product) {
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.detail = product.getDetail();
        this.image = product.getImage();
        this.categoryId = product.getCategory() != null ? product.getCategory().getCategoryId() : 1;
        this.discountId = product.getCoupon() != null ? product.getCoupon().getCouponId() : -1;
        this.postingDate = product.getPostingDate();
        this.unit = product.getUnit();
        // Lưu variations từ product (vì fetch type là EAGER)
        this.variations = new HashSet<>(product.getVariation());
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDetail() {
        return detail;
    }

    public String getImage() {
        return image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getDiscountId() {
        return discountId;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public String getUnit() {
        return unit;
    }

    public Set<Variation> getVariations() {
        return new HashSet<>(variations);
    }
}