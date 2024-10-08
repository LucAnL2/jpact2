package vn.iotstar.controllers.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.services.ICategoryService;
import vn.iotstar.services.impl.CategoryService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import vn.iotstar.utils.Constant;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/edit", "/admin/category/update",
		"/admin/category/insert", "/admin/category/add", "/admin/category/delete" })
public class CategoryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ICategoryService cateService = new CategoryService();

	public CategoryController() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String url = request.getRequestURI();
		if (url.contains("categories")) {
			List<Category> list = cateService.findAll();
			request.setAttribute("listcate", list);
			request.getRequestDispatcher("/views/admin/category-list.jsp").forward(request, response);
		} else if (url.contains("/admin/category/edit")) {
			int id = Integer.parseInt(request.getParameter("id"));
			Category category = cateService.findById(id);
			request.setAttribute("cate", category);
			request.getRequestDispatcher("/views/admin/category-edit.jsp").forward(request, response);
		} else if (url.contains("/admin/category/add")) {
			request.getRequestDispatcher("/views/admin/category-add.jsp").forward(request, response);
		} else if (url.contains("/admin/category/delete")) {
			int id = Integer.parseInt(request.getParameter("id"));
			try {
				cateService.delete(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.sendRedirect(request.getContextPath() + "/admin/categories");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String url = request.getRequestURI();
		if (url.contains("/admin/category/update")) {
			int categoryid = Integer.parseInt(request.getParameter("categoryid"));
			String categoryname = request.getParameter("categoryname");
			int status = Integer.parseInt(request.getParameter("status"));
			Category category = new Category();
			category.setCategoryId(categoryid);
			category.setCategoryname(categoryname);
			category.setStatus(status);
			// lưu hình cũ
			Category cateold = cateService.findById(categoryid);
			String fileold = cateold.getImages();
			// xử lý image
			String fname = "";
			String uploadPath = Constant.UPLOAD_DIRECTORY;
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			try {

				Part part = request.getPart("images");
				if (part.getSize() > 0) {
					String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
					// đổi tên file
					int index = filename.lastIndexOf(".");
					String ext = filename.substring(index + 1);
					fname = System.currentTimeMillis() + "." + ext;
					// up load file
					part.write(uploadPath + "/" + fname);
					// ghi tên file vào data
					category.setImages(fname);
				} else {
					category.setImages(fileold);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			cateService.update(category);
			response.sendRedirect(request.getContextPath() + "/admin/categories");
		} else if (url.contains("/admin/category/insert")) {
			Category category = new Category();
			String categoryname = request.getParameter("categoryname");
			int status = Integer.parseInt(request.getParameter("status"));
			category.setCategoryname(categoryname);
			category.setStatus(status);
			String fname = "";
			String uploadPath = Constant.UPLOAD_DIRECTORY;
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			try {
				Part part = request.getPart("images");
				if (part.getSize() > 0) {
					String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
					// đổi tên file
					int index = filename.lastIndexOf(".");
					String ext = filename.substring(index + 1);
					fname = System.currentTimeMillis() + "." + ext;
					// up load file
					part.write(uploadPath + "/" + fname);
					// ghi tên file vào data
					category.setImages(fname);
				} else {
					category.setImages("avata.png");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			cateService.insert(category);
			response.sendRedirect(request.getContextPath() + "/admin/categories");
		}

	}
}
