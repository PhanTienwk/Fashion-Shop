<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/views/include/library.jsp"%>
<script src="<c:url value="/assets/js/employee/EmployeeProductFormHandler.js"/>"></script>
<style>
.hidden {
    display: none !important;
}

/* Cải thiện thiết kế nút hoàn tác */
.undo-actions {
    display: flex;
    gap: 8px;
    align-items: center;
}

.undo-btn {
    font-size: 0.75rem;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    text-decoration: none;
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

.undo-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.undo-btn i {
    font-size: 0.7rem;
}

/* Notification cải thiện */
.undo-notification {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 1050;
    min-width: 300px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.undo-notification .alert {
    margin-bottom: 0;
    border: none;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
}

.undo-notification .undo-action {
    margin-left: 12px;
}

.undo-notification .undo-action .btn {
    font-size: 0.75rem;
    padding: 0.25rem 0.75rem;
}

/* Animation cho highlight row */
@keyframes highlightRow {
    0% { background-color: rgba(255, 193, 7, 0.3); }
    100% { background-color: transparent; }
}

.recently-updated {
    animation: highlightRow 2s ease-out;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .undo-actions {
        flex-direction: column;
        gap: 4px;
    }
    
    .undo-btn {
        font-size: 0.7rem;
        padding: 0.2rem 0.4rem;
    }
}
</style>
<body>

<!-- Notification cải thiện cho hoàn tác -->
<% 
    Integer lastUpdatedProductId = (Integer) session.getAttribute("lastUpdatedProductId");
    Object memento = session.getAttribute("lastProductMemento");
    if (lastUpdatedProductId != null && memento != null) {
%>
<div class="undo-notification">
    <div class="alert alert-warning alert-dismissible fade show" role="alert">
        <div class="d-flex align-items-center">
            <i class="fas fa-info-circle me-2"></i>
            <span>Sản phẩm vừa được cập nhật thành công</span>
        </div>
        <div class="undo-action">
            <form action="<c:url value='/employee/products/undo-update.htm' />" method="get" style="display: inline;">
                <input type="hidden" name="productId" value="<%= lastUpdatedProductId %>" />
                <button type="submit" class="btn btn-outline-warning btn-sm">
                    <i class="fas fa-undo me-1"></i>Hoàn tác
                </button>
            </form>
            <button type="button" class="btn-close ms-2" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</div>
<%
    }
%>

    <!-- Thông báo alert cải thiện -->
    <c:choose>
        <c:when test="${alert == 3}">
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 1050">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    Thêm sản phẩm thành công
                    <button type="button" class="ms-auto btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </c:when>
        <c:when test="${alert == 4}">
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 1050">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-edit me-2"></i>
                    Cập nhật sản phẩm thành công
                    <button type="button" class="ms-auto btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </c:when>
        <c:when test="${alert == 5}">
            <div class="position-fixed top-0 end-0 p-3" style="z-index: 1050">
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <i class="fas fa-undo me-2"></i>
                    Hoàn tác thành công
                    <button type="button" class="ms-auto btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
        </c:when>
    </c:choose>

    <div class="row">
        <!-- Sidebar -->
        <div class="col-2 d-none d-lg-inline">
            <%@include file="/WEB-INF/views/employee/employee-header-nav.jsp"%>
        </div>
        <div class="col-10 col-12-sm col-12-md">
            <div id="content-wrapper" class="d-flex flex-column">
                <nav class="navbar navbar-light bg-white mb-4 static-top shadow d-none d-lg-inline">
                    <%@include file="/WEB-INF/views/admin/admin-topbar.jsp"%>
                </nav>
                <div class="container">
                    <!-- Breadcrum -->
                    <div class="row mb-8">
                        <div class="col-md-12">
                            <div class="d-md-flex justify-content-between align-items-center">
                                <div>
                                    <h2>Product</h2>
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb mb-0 text-muted fs-6 fw-semibold">
                                            <li class="breadcrumb-item">
                                                <a href="employee/products.htm" class="text-decoration-none text-success">Product</a>
                                            </li>
                                        </ol>
                                    </nav>
                                </div>
                                <div>
                                    <a type="button" href="employee/products/create-product.htm" class="btn btn-success">
                                        <i class="fas fa-plus me-1"></i>Add Products
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--End Breadcrum -->
                    <div class="row">
                        <div class="col-xl-12 col-12 mb-5">
                            <div class="px-6 py-6 p-4">
                                <div class="row justify-content-between">
                                    <div class="col-lg-4 col-md-6 col-12 mb-2 mb-md-0">
                                        <form class="d-flex" role="search" action="employee/products/searchProduct.htm">
                                            <div class="input-group">
                                                <input class="form-control" type="search" placeholder="Tìm kiếm sản phẩm..." aria-label="Search" name="search">
                                                <button class="btn btn-outline-success" type="submit">
                                                    <i class="fas fa-search"></i>
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- table -->
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="position-sticky top-0">
                                <tr class="table-success">
                                    <th>Hình ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Danh mục</th>
                                    <th>Ngày đăng</th>
                                    <th>Số lượng</th>
                                    <th>Chi tiết</th>
                                    <th>Giá</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach varStatus="status" var="item" items="${products}">
                                    <tr class="${(alert == 4 and lastUpdatedProductId == item.productId) ? 'recently-updated' : ''}">
                                        <td class="align-middle">
                                            <a href="product/detail.htm?productId=${item.productId}">
                                                <img src="<c:url value='assets/img/products/${item.image}'/>" alt="Product Name" 
                                                     style="width: 80px; border-radius: 8px; object-fit: cover;">
                                            </a>
                                        </td>
                                        <td class="align-middle">
                                            <a href="product/detail.htm?productId=${item.productId}" class="text-dark fw-semibold">${item.productName}</a>
                                        </td>
                                        <td class="align-middle">
                                            <input type="hidden" id="categoryId${item.productId}" value="${item.categoryId}">
                                            <span class="badge bg-light text-dark">${item.categoryName}</span>
                                        </td>
                                        <td class="align-middle">
                                            <fmt:formatDate value="${item.postingDate}" pattern="dd-MM-yyyy"/>
                                            <input type="hidden" id="postingDate${item.productId}" value="${item.postingDate}">
                                        </td>
                                        <td class="align-middle">
                                            <span class="badge ${item.quantity > 10 ? 'bg-success' : item.quantity > 0 ? 'bg-warning' : 'bg-danger'}">
                                                ${item.quantity}
                                            </span>
                                        </td>
                                        <td class="align-middle">
                                            <p class="text-truncate mb-0" style="max-width: 100px;" title="${item.detail}">${item.detail}</p>
                                            <input type="hidden" id="detail${item.productId}" value="${item.detail}"/>
                                        </td>
                                        <td class="align-middle">
                                            <c:if test="${item.discount > 0}">
                                                <div class="d-flex flex-column">
                                                    <span class="text-dark fw-bold">
                                                        <fmt:formatNumber value="${item.price - (item.price * item.discount)}" type="currency" currencySymbol="VND" maxFractionDigits="0"/>
                                                    </span>
                                                    <span class="text-decoration-line-through text-muted small">
                                                        <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="VND" maxFractionDigits="0"/>
                                                    </span>
                                                </div>
                                            </c:if>
                                            <c:if test="${item.discount == 0}">
                                                <span class="text-dark fw-bold">
                                                    <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="VND" maxFractionDigits="0"/>
                                                </span>
                                            </c:if>
                                        </td>
                                        <td class="align-middle">
                                            <div class="undo-actions">
                                                <a href="employee/products/update-product.htm?id=${item.productId}" 
                                                   class="btn btn-primary btn-sm" type="button">
                                                    <i class="fas fa-edit me-1"></i><span class="d-none d-md-inline">Sửa</span>
                                                </a>
                                                
                                                <!-- Nút Hoàn tác được thiết kế lại -->
                                                <c:if test="${alert == 4 and lastUpdatedProductId == item.productId}">
                                                    <a href="employee/products/undo-update.htm?productId=${item.productId}" 
                                                       class="btn btn-warning btn-sm undo-btn" 
                                                       type="button"
                                                       title="Hoàn tác thay đổi vừa rồi"
                                                       onclick="return confirm('Bạn có chắc chắn muốn hoàn tác thay đổi này không?')">
                                                        <i class="fas fa-undo"></i>
                                                        <span class="d-none d-lg-inline">Hoàn tác</span>
                                                    </a>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="d-flex justify-content-center">
                    <!-- nav -->
                    <nav>
                        <ul class="pagination d-flex justify-content-center ms-2">
                            <li class="page-item ${(crrPage == 1) ? 'disabled' : ''}">
                                <a class="page-link mx-1" aria-label="Previous" href="employee/products.htm?crrPage=${crrPage - 1}&filter=${filter}">
                                    <span aria-hidden="true">«</span>
                                </a>
                            </li>
                            <c:forEach var="i" begin="1" end="${totalPage}" varStatus="in">
                                <li class="page-item">
                                    <a class="page-link mx-1 ${(crrPage == in.count) ? 'active' : ''}" href="employee/products.htm?crrPage=${in.count}&filter=${filter}">${in.count}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item">
                                <a class="page-link mx-1 text-body ${(crrPage == totalPage) ? 'disabled' : ''}" aria-label="Next" href="employee/products.htm?crrPage=${crrPage + 1}&filter=${filter}">
                                    <span aria-hidden="true">»</span>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <script src="<c:url value='assets/js/admin/AlertHandler.js'/>"></script>
    <script>
        // Auto hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
        
        // Add smooth scroll to recently updated item
        document.addEventListener('DOMContentLoaded', function() {
            const recentlyUpdated = document.querySelector('.recently-updated');
            if (recentlyUpdated) {
                recentlyUpdated.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    </script>
</body>
</html>