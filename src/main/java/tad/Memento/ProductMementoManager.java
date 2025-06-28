package tad.Memento;

import tad.entity.Product;
import tad.entity.Variation;

import java.util.HashSet;

public class ProductMementoManager {
    private Product product;

    public ProductMementoManager(Product product) {
        this.product = product;
    }

    public ProductMemento save() {
        return new ProductMemento(product);
    }

    public void restore(ProductMemento memento) {
        product.setProductName(memento.getProductName());
        product.setPrice(memento.getPrice());
        product.setQuantity(memento.getQuantity());
        product.setDetail(memento.getDetail());
        product.setImage(memento.getImage());
        product.setPostingDate(memento.getPostingDate());
        product.setUnit(memento.getUnit());
        if (product.getVariation() == null) {
            product.setVariation(new HashSet<Variation>());
        }
        product.getVariation().clear();
        product.getVariation().addAll(memento.getVariations());
    }
}