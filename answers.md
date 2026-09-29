# Lab 4: Server-Rendered CRUD Interface

## Overview
* **Branch:** week4
* **Port:** 8082

## Task Summary

### Task 1 - Product List Template
* **File:** `src/main/resources/templates/products.html`
* **What I did:** Built the catalog page by looping over the products with `th:each` and showing each name with `th:text`. A `th:if` check displays a "no products" message only when the list is empty.
* **Evidence:** `screenshots/products_list.png`

### Task 2 - Product Detail Template
* **File:** `src/main/resources/templates/product-detail.html`
* **What I did:** Showed the product's ID, name, and price using variable expressions. The category name is read through the nested property `${product.category?.name}`. The `?.` safe-navigation operator prevents an error if a product has no category.
* **Evidence:** `screenshots/product_detail.png`

### Task 3 - Shared Navigation Fragment
* **File:** `src/main/resources/templates/fragments/navbar.html`
* **What I did:** Moved the navigation links into one reusable fragment (`th:fragment="navbar"`). `products.html`, `product-detail.html`, and `product-form.html` all include it with `th:replace`, so the markup lives in a single file.
* **Evidence:** `screenshots/navbar_list.png`, `screenshots/navbar_detail.png`

### Task 4 - Create Form
* **File:** `src/main/resources/templates/product-form.html`
* **What I did:** Made a form tied to a `Product` object with `th:object`, with its inputs connected through `th:field`. After a successful save, the controller redirects to `/products`.
* **Evidence:** `screenshots/form_submit.png`

### Task 5 - Binding Errors on the Create Form
* **Files:** `ProductController.java`, `product-form.html`, `messages.properties`
* **What I did:** In the `@PostMapping` method, `BindingResult` comes directly after `@ModelAttribute("product")` so type-conversion failures (such as text typed into the price field) are captured instead of crashing. If `result.hasErrors()` is true, the form is shown again and nothing is saved. Each field's error appears next to it using `th:errors`. A custom `typeMismatch.price` message makes the price error easier to read, and a blank name is also rejected.
* **Evidence:** `screenshots/binding_error.png`