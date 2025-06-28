package tad.controller.employee;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tad.DAO.ICategoryDAO;
import tad.DAO.ICouponDAO;
import tad.DAO.IProductDAO;
import tad.DAO.ISizeDAO;
import tad.Memento.ProductMemento;
import tad.Memento.ProductMementoManager;
import tad.bean.ProductBean;
import tad.bean.UploadFile;
import tad.bean.VariationBean;
import tad.entity.Account;
import tad.entity.Category;
import tad.entity.Coupon;
import tad.entity.Product;
import tad.entity.Size;
import tad.entity.Variation;
import tad.utility.Constants;
import tad.utility.DefineAttribute;

@Controller
@RequestMapping("/employee/products")
public class EmployeeControllerProducts {

	@Autowired
	private ICategoryDAO categoryDAO;

	@Autowired
	private IProductDAO productDAO;
	
	@Autowired
	private ISizeDAO sizeDAO;

	@Autowired
	@Qualifier("productImgDir")
	private UploadFile productImgDir;

	@RequestMapping()
	public String gProductList(ModelMap model, HttpSession session,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage) {
		Account currentAcc = (Account) session.getAttribute(DefineAttribute.UserAttribute);
		if (currentAcc == null) {
			return "redirect:/guest.htm";
		}

		List<ProductBean> products = new ArrayList<>();
		List<Product> listProducts = productDAO.listProducts(currentAcc.getAccountId());
		for (Product product : listProducts) {
			ProductBean bean = new ProductBean(product);
			products.add(bean);
		}

		int startIndex = (crrPage - 1) * Constants.PRODUCT_PER_PAGE_IN_HOME;
		int totalPage = 1;
		if (products.size() <= Constants.PRODUCT_PER_PAGE_IN_HOME)
			totalPage = 1;
		else {
			totalPage = products.size() / Constants.PRODUCT_PER_PAGE_IN_HOME;
			if (products.size() % Constants.PRODUCT_PER_PAGE_IN_HOME != 0) {
				totalPage++;
			}
		}

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("crrPage", crrPage);
		model.addAttribute("products", products.subList(startIndex,
				Math.min(startIndex + Constants.PRODUCT_PER_PAGE_IN_HOME, products.size())));

		return "employee/employee-product";
	}


	@Autowired
	@Qualifier("productImgDir")
	private UploadFile uploadProductImg;

	@Autowired
	private ICouponDAO couponDAO;

	@RequestMapping(value = "update-product")
	public String updateProduct(ModelMap model, @RequestParam("id") int id, HttpSession session) {
	    Account currentAcc = (Account) session.getAttribute(DefineAttribute.UserAttribute);
	    Product findProduct = productDAO.getProduct(id);
	    ProductBean beanForm = new ProductBean(findProduct);
	    model.addAttribute("productForm", beanForm);
	    Account tacc = couponDAO.FetchAccountCoupon(currentAcc);
	    model.addAttribute("categories", categoryDAO.getListCategories());
	    model.addAttribute("coupons", tacc.getCoupons());
	    
	    // Lấy số lượng hiện tại của các kích thước
	    Map<Integer, Integer> variationQuantities = new HashMap<>();
	    List<Variation> variations = productDAO.listVariation().stream()
	        .filter(v -> v.getProduct().getProductId() == id)
	        .collect(Collectors.toList());
	    for (Variation v : variations) {
	        variationQuantities.put(v.getSize().getSizeId(), v.getQuantity());
	    }
	    
	    model.addAttribute("variationQuantities", variationQuantities);
	    model.addAttribute("mess", 2);
	    return "employee/employee-createProduct";
	}

	@RequestMapping(value = "update-product", method = RequestMethod.POST)
	public String pUpdateProduct(ModelMap model, @ModelAttribute("productForm") ProductBean product, 
	                             HttpServletRequest request, HttpSession session) {
	    Product findProduct = productDAO.getProduct(product.getProductId());
	    
	    if (findProduct == null) {
	        model.addAttribute("message", "Sản phẩm không tồn tại.");
	        return "employee/employee-updateProduct";
	    }

	    // Lưu trạng thái sản phẩm trước khi cập nhật
	    ProductMementoManager mementoManager = new ProductMementoManager(findProduct);
	    ProductMemento memento = mementoManager.save();

	    // Lưu memento vào session để hoàn tác
	    session.setAttribute("lastProductMemento", memento);
	    session.setAttribute("lastUpdatedProductId", findProduct.getProductId());

	    try {
	        // Cập nhật danh mục
	        Category category = categoryDAO.getCategory(product.getCategoryId());
	        if (category != null) {
	            findProduct.setCategory(category);
	        }

	        // Cập nhật coupon
	        if (product.getDiscountId() != -1) {
	            Coupon coupon = couponDAO.getCoupon(product.getDiscountId());
	            if (coupon != null) {
	                findProduct.setCoupon(coupon);
	            }
	        }

	        // Cập nhật ảnh
	        if (product.getImageFile() != null && !product.getImageFile().isEmpty()) {
	            String filename = product.getImageFile().getOriginalFilename();
	            File imageFile = new File(productImgDir.getPath() + File.separator + filename);
	            if (imageFile.exists()) {
	                imageFile.delete();
	            }
	            product.getImageFile().transferTo(imageFile);
	            findProduct.setImage(filename);
	        }

	        // Cập nhật biến thể
	        productDAO.clearProductVariations(findProduct.getProductId());
	        for (int i = 1; i <= 8; i++) {
	            int quantity = parseInteger(request.getParameter(String.valueOf(i)));
	            if (quantity > 0) {
	                Variation variation = new Variation();
	                variation.setProduct(findProduct);
	                Size size = sizeDAO.getSize(i);
	                if (size != null) {
	                    variation.setSize(size);
	                    variation.setQuantity(quantity);
	                    productDAO.addVariation(variation);
	                    System.out.println("Variation saved: SizeID=" + i + ", Quantity=" + quantity);
	                } else {
	                    System.out.println("Size not found for SizeID: " + i);
	                }
	            }
	        }

	        // Cập nhật các trường khác
	        findProduct.setProductName(product.getProductName());
	        findProduct.setPrice(product.getPrice());
//	        findProduct.setQuantity(product.getQuantity());
	        findProduct.setDetail(product.getDetail());
	        findProduct.setPostingDate(product.getPostingDate());
	        findProduct.setUnit(product.getUnit());

	        // Gọi update
	        boolean updated = productDAO.updateProduct(findProduct);
	        if (!updated) {
	            throw new Exception("Lỗi khi cập nhật sản phẩm.");
	        }

	        model.addAttribute("alert", 4); // Cập nhật thành công
	        model.addAttribute("lastUpdatedProductId", findProduct.getProductId());
	        return "redirect:/employee/products.htm";
	    } catch (Exception e) {
	        // Khôi phục trạng thái nếu có lỗi
	        mementoManager.restore(memento);
	        productDAO.updateProduct(findProduct); // Lưu trạng thái khôi phục vào DB
	        session.removeAttribute("lastProductMemento"); // Xóa memento khỏi session
	        session.removeAttribute("lastUpdatedProductId");
	        e.printStackTrace();
	        model.addAttribute("message", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
	        return "employee/employee-updateProduct";
	    }
	}
	
	@RequestMapping(value = "undo-update")
	public String undoUpdate(RedirectAttributes redirectAttributes, @RequestParam("productId") int productId, HttpSession session) {
	    Product findProduct = productDAO.getProduct(productId);
	    ProductMemento memento = (ProductMemento) session.getAttribute("lastProductMemento");
	    Integer lastUpdatedProductId = (Integer) session.getAttribute("lastUpdatedProductId");

	    if (findProduct == null || memento == null || lastUpdatedProductId == null || lastUpdatedProductId != productId) {
	        redirectAttributes.addFlashAttribute("message", "Không thể hoàn tác: Không tìm thấy trạng thái trước đó.");
	        return "redirect:/employee/products.htm";
	    }

	    try {
	        ProductMementoManager mementoManager = new ProductMementoManager(findProduct);
	        mementoManager.restore(memento);

	        productDAO.clearProductVariations(findProduct.getProductId());
	        for (Variation v : memento.getVariations()) {
	            Variation newVariation = new Variation();
	            newVariation.setProduct(findProduct);
	            newVariation.setSize(v.getSize());
	            newVariation.setQuantity(v.getQuantity());
	            productDAO.addVariation(newVariation);
	        }

	        boolean updated = productDAO.updateProduct(findProduct);
	        if (!updated) {
	            throw new Exception("Lỗi khi hoàn tác cập nhật.");
	        }

	        session.removeAttribute("lastProductMemento");
	        session.removeAttribute("lastUpdatedProductId");

	        redirectAttributes.addFlashAttribute("alert", 5); // Hoàn tác thành công
	        return "redirect:/employee/products.htm";
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("message", "Lỗi khi hoàn tác: " + e.getMessage());
	        return "redirect:/employee/products.htm";
	    }
	}


	@RequestMapping("searchProduct")
	public String search(@RequestParam(required = false, value = "search") String search,
			@RequestParam(required = false, value = "currPage", defaultValue = "1") int crrPage, ModelMap modelMap) {

		List<ProductBean> products = new ArrayList<>();
		List<Product> listProducts = productDAO.filterProductByName(search);
		for (Product p : listProducts) {
			ProductBean bean = new ProductBean(p);
			products.add(bean);
		}

		int startIndex = (crrPage - 1) * Constants.PRODUCT_PER_PAGE_IN_HOME;
		int totalPage = 1;
		if (products.size() <= Constants.PRODUCT_PER_PAGE_IN_HOME)
			totalPage = 1;
		else {
			totalPage = products.size() / Constants.PRODUCT_PER_PAGE_IN_HOME;
			if (products.size() % Constants.PRODUCT_PER_PAGE_IN_HOME != 0) {
				totalPage++;
			}
		}

		modelMap.addAttribute("products", products.subList(startIndex,
				Math.min(startIndex + Constants.PRODUCT_PER_PAGE_IN_CATEGORY, products.size())));
		modelMap.addAttribute("crrPage", crrPage);
		modelMap.addAttribute("totalPage", totalPage);
		return "employee/employee-product";
	}

	@RequestMapping(value = "create-product.htm", method = RequestMethod.GET)
	public String createProduct(ModelMap model, HttpSession session) {

		Account currentAcc = (Account) session.getAttribute(DefineAttribute.UserAttribute);
		if (currentAcc == null) {
			return "redirect:/guest.htm";
		}

		ProductBean beanForm = new ProductBean();
		model.addAttribute("productForm", beanForm);
		Account tacc = couponDAO.FetchAccountCoupon(currentAcc);
		Set<Coupon> coupons = tacc.getCoupons();
		model.addAttribute("categories", categoryDAO.getListCategories());
		model.addAttribute("coupons", coupons);
		model.addAttribute("mess", 1);
		return "employee/employee-createProduct";
	}

	@RequestMapping(value = "create-product.htm")
	public String pCreateProduct(ModelMap model, @ModelAttribute("productForm") ProductBean product,
	        HttpSession session, HttpServletRequest request) {

	    Account acc = (Account) session.getAttribute(DefineAttribute.UserAttribute);
	    if (acc == null) {
	        return "redirect:/guest.htm";
	    }

	    Product newProduct = new Product();
	    newProduct.setAccount(acc);

	    Category category = categoryDAO.getCategory(product.getCategoryId());
	    if (category != null) {
	        newProduct.setCategory(category);
	    } else {
	        return "employee/employee-createProduct";
	    }

	    if (product.getDiscountId() != -1) {
	        Coupon coupon = couponDAO.getCoupon(product.getDiscountId());
	        if (coupon != null) {
	            newProduct.setCoupon(coupon);
	        }
	    }

	    // Upload ảnh
	    if (product.getImageFile() != null && !product.getImageFile().isEmpty()) {
	        String originalFilename = product.getImageFile().getOriginalFilename();
	        File file = new File(productImgDir.getPath() + File.separator + originalFilename);

	        if (file.exists()) {
	            file.delete();
	        }

	        try {
	            product.getImageFile().transferTo(file);
	            newProduct.setImage(originalFilename);
	        } catch (Exception e) {
	            e.printStackTrace();
	            model.addAttribute("message", 1);
	            model.addAttribute("productBean", product);
	            return "employee/employee-createProduct";
	        }
	    }

	    newProduct.setProductName(product.getProductName());
	    newProduct.setDetail(product.getDetail());

	    // Thời gian đăng
	    Date now = new Date();
	    newProduct.setPostingDate(now);

	    // Chưa set quantity, vì sẽ tính tổng từ các variation sau
	    newProduct.setQuantity(0);

	    // Thêm sản phẩm trước để có ID
	    if (!productDAO.insertProduct(newProduct)) {
	        model.addAttribute("mess", "Lỗi khi thêm sản phẩm");
	        return "employee/employee-createProduct";
	    }

	    // Lấy sản phẩm mới thêm từ DB (vì cần ID)
	    Product insertedProduct = productDAO.getProduct(productDAO.ProductMax().getProductId());
	    int totalQuantity = 0;

	    // Thêm các Variation và tính tổng số lượng
	    for (int i = 1; i <= 8; i++) {
	        int quantity = parseInteger(request.getParameter(String.valueOf(i)));
	        if (quantity < 0) quantity = 0;

	        Variation variation = new Variation();
	        variation.setProduct(insertedProduct);
	        variation.setSize(sizeDAO.getSizeBySizeID(i));
	        variation.setQuantity(quantity);

	        sizeDAO.insertVariation(variation);
	        totalQuantity += quantity;
	    }

	    // Cập nhật tổng số lượng vào Product
	    insertedProduct.setQuantity(totalQuantity);
	    productDAO.updateProduct(insertedProduct); // Gọi DAO để update lại Product đã có ID

	    return "redirect:/employee/products.htm";
	}

	
	private Integer parseInteger(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0; 
    }
}
