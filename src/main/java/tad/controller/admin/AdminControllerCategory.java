package tad.controller.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tad.DAO.ICategoryDAO;
import tad.Iterator.CategoryIterator;
import tad.Iterator.CategoryListIterator;
import tad.Memento.CategoryMemento;
import tad.Memento.CategoryMementoCaretaker;
import tad.bean.CategoryBean;
import tad.bean.UploadFile;
import tad.entity.Category;
import tad.utility.Constants;

@Controller
@RequestMapping("/admin/category")
public class AdminControllerCategory {

	@Autowired
	private ICategoryDAO categoryDAO;

	@Autowired
	@Qualifier("categoryImgDir")
	private UploadFile categoryImgDir;

	//Áp dụng Iterator Pattern
	@RequestMapping()
	public String gCategoryList(
	        ModelMap model,
	        @RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
	        @RequestParam(value = "filter", defaultValue = "0") int filter) {

	    List<Category> allCategories = categoryDAO.getListCategories();

	    int totalCategories = allCategories.size();
	    int totalPage = (int) Math.ceil((double) totalCategories / Constants.USER_PER_PAGE);

	    int startIndex = (crrPage - 1) * Constants.USER_PER_PAGE;
	    int endIndex = Math.min(startIndex + Constants.USER_PER_PAGE, totalCategories);

	    //Duyệt bằng Iterator và chỉ lấy những phần tử trong khoảng [startIndex, endIndex)
	    List<Category> pageList = new ArrayList<>();

	    //dùng trực tiếp CategoryListIterator trên List<Category>:
	    CategoryIterator it = new CategoryListIterator(allCategories);
	    int idx = 0;
	    while (it.hasNext()) {
	        Category c = it.next();
	        if (idx >= startIndex && idx < endIndex) {
	            pageList.add(c);
	        }
	        idx++;
	        if (idx >= endIndex) {
	            break; // đã thu đủ phần tử trang, thoát sớm
	        }
	    }

	    //Chuyển pageList thành List<CategoryBean> (fetch sản phẩm)
	    List<CategoryBean> categoriesBean = CategoryBean.ConvertListCategory(pageList, categoryDAO);

	    if (filter == 1) {
	        // Chỉ những bean có products.size() == 0
	        categoriesBean = categoriesBean.stream()
	                            .filter(b -> b.getProducts().isEmpty())
	                            .collect(Collectors.toList());
	    } else if (filter == 2) {
	        // Chỉ những bean có products.size() > 0
	        categoriesBean = categoriesBean.stream()
	                            .filter(b -> !b.getProducts().isEmpty())
	                            .collect(Collectors.toList());
	    }

	    model.addAttribute("totalPage", totalPage);
	    model.addAttribute("crrPage", crrPage);
	    model.addAttribute("list", categoriesBean);
	    model.addAttribute("filter", filter);

	    return "admin/admin-category";
	}


	@RequestMapping("searchCategory")
	public String gCategoryWithSearch(@RequestParam(required = false, value = "search") String search,
			@RequestParam(required = false, value = "crrPage", defaultValue = "1") int crrPage, ModelMap model) {
		List<Category> categories = categoryDAO.searchCategory(search);
		int startIndex = (crrPage - 1) * Constants.USER_PER_PAGE;
		int totalPage = 1;
		if (categories.size() <= Constants.USER_PER_PAGE)
			totalPage = 1;
		else {
			totalPage = categories.size() / Constants.USER_PER_PAGE;
			if (categories.size() % Constants.USER_PER_PAGE != 0) {
				totalPage++;
			}
		}

		model.addAttribute("crrPage", crrPage);
		model.addAttribute("totalPage", totalPage);
		ArrayList<CategoryBean> categoriesBean = CategoryBean.ConvertListCategory(categories.subList(startIndex,
				Math.min(startIndex + Constants.PRODUCT_PER_PAGE_IN_HOME, categories.size())), categoryDAO);
		model.addAttribute("list", categoriesBean);
		model.addAttribute("filter", 0);
		return "admin/admin-category";
	}

	@RequestMapping("add")
	public String gCategoryAdd(ModelMap modelMap) {
		Category category = new Category();
		CategoryBean categoryBean = new CategoryBean(category.getCategoryId(), category.getName(), category.getImage());
		modelMap.addAttribute("addBean", categoryBean);
		return "admin/admin-category-form";
	}

	@RequestMapping("update{id}")
	public String gCategoryUpdate(@PathVariable("id") int id, ModelMap modelMap) {
		Category category = categoryDAO.getCategory(id);
		CategoryBean categoryBean = new CategoryBean(category);
		modelMap.addAttribute("updateBean", categoryBean);
		return "admin/admin-category-form";
	}

	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String pCategoryAdd(@ModelAttribute("addBean") CategoryBean categoryBean, ModelMap model) {
		if (categoryBean != null) {
			Category category = new Category();

			category.setName(categoryBean.getName());
			
			// Chỉ xử lý khi có file ảnh được upload
	        if (categoryBean.getFileImage() != null && !categoryBean.getFileImage().isEmpty()) {
	            // Đảm bảo thư mục lưu ảnh tồn tại
	            File uploadDir = new File(categoryImgDir.getPath());
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }

	            // Lấy tên file gốc
	            String fileName = categoryBean.getFileImage().getOriginalFilename();
	            // Tạo đối tượng File với đường dẫn: [folder] + separator + [fileName]
	            File file = new File(categoryImgDir.getPath() + File.separator + fileName);

	            // Nếu đã có file trùng tên, xóa để tránh ghi đè
	            if (file.exists()) {
	                file.delete();
	            }

	            try {
	                // Ghi file lên filesystem
	                categoryBean.getFileImage().transferTo(file);
	                // Lưu tên file vào entity để ghi vào database
	                category.setImage(fileName);
	            } catch (Exception e) {
	                e.printStackTrace();
	                model.addAttribute("message", 1);
	                model.addAttribute("categoryBean", categoryBean);
	                return "admin/admin-category-form";
	            }
	        }
			categoryDAO.addCategory(category);
			model.addAttribute("message", 2);
		}

		return "redirect:/admin/category.htm";
	}

	@Autowired
	@Qualifier("categoryImgDir")
	private UploadFile uploadCategory;

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String pCategoryUpdate(@ModelAttribute("updateBean") CategoryBean categoryBean, ModelMap model) {
		Category category = categoryDAO.getCategory(categoryBean.getId());
		if (category != null) {
			category.setName(categoryBean.getName());

			if (categoryBean.getFileImage() != null && !categoryBean.getFileImage().isEmpty()) {
	            // Đảm bảo thư mục upload tồn tại
	            File uploadDir = new File(categoryImgDir.getPath());
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }

	            // Lấy tên file
	            String fileName = categoryBean.getFileImage().getOriginalFilename();
	            File file = new File(categoryImgDir.getPath() + File.separator + fileName);

	            // Nếu file trùng tên đã tồn tại, xóa
	            if (file.exists()) {
	                file.delete();
	            }
	            
	            System.out.println("File: " + file);
	            
	            try {
	                // Upload ảnh
	                categoryBean.getFileImage().transferTo(file);
	                // Cập nhật tên ảnh trong DB
	                category.setImage(fileName);
	                
	                System.out.println("File name: " + fileName);
	            } catch (Exception e) {
	                e.printStackTrace();
	                model.addAttribute("message", 1);
	                model.addAttribute("categoryBean", categoryBean);
	                return "admin/admin-category-form";
	            }
	        }
			categoryDAO.updateCategory(category);
			model.addAttribute("message", 2);

		}

		return "redirect:/admin/category.htm";
	}
	
	@Autowired
	private CategoryMementoCaretaker caretaker;
	
	@Autowired
	private HttpSession session;

	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public String pCategoryDelete(@RequestParam("id") int id, RedirectAttributes reAttributes) {
		Category category = categoryDAO.getCategory(id);
		if (category != null) {
			
	        CategoryMemento memento = category.saveToMemento();
	        caretaker.saveMemento(id, memento);
	        
	        session.setAttribute("lastDeletedCategoryMemento", memento);
	        
	        boolean deleted = categoryDAO.deleteCategory(category);
	        if (deleted) {
	            reAttributes.addFlashAttribute("alert", 2); // mã 2 = xóa thành công
	        } else {
	            reAttributes.addFlashAttribute("alert", 1); // mã 1 = lỗi
	        }
	    } else {
	        reAttributes.addFlashAttribute("alert", 1);
	    }

		return "redirect:/admin/category.htm";
	}
	
	@RequestMapping(value = "undo-delete", method = RequestMethod.GET)
    public String undoDeletedCategory(RedirectAttributes reAttributes) {
        
        // Giải pháp đơn giản: thêm method getLastDeleted() vào Caretaker
        CategoryMemento memento = caretaker.getLastDeleted();
        
        if (memento != null) {
            Category restored = new Category();
            
            // Sử dụng restoreFromMemento để khôi phục trạng thái
            restored.restoreFromMemento(memento);
            
            boolean success = categoryDAO.restoreCategory(restored);
            
            if (success) {
                reAttributes.addFlashAttribute("alert", 3);
                caretaker.removeMemento(memento.getId());
                session.removeAttribute("lastDeletedCategoryMemento");
                
            } else {
                reAttributes.addFlashAttribute("alert", 1);
            }
        } else {
            reAttributes.addFlashAttribute("alert", 1);
        }
        
        return "redirect:/admin/category.htm";
    }

}
