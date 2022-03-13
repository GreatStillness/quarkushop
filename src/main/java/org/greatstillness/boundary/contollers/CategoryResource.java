package org.greatstillness.boundary.contollers;

import org.greatstillness.control.dtos.CategoryDto;
import org.greatstillness.control.dtos.ProductDto;
import org.greatstillness.control.services.CategoryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("/categories")
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @GET
    public List<CategoryDto> findAll() {
        return this.categoryService.findAll();
    }

    @GET
    @Path("/{id}/products")
    public List<ProductDto> findProductsByCategoryId(@PathParam("id") Long id) {
        return this.categoryService.findProductsByCategoryId(id);
    }

    @POST
    public CategoryDto create(CategoryDto categoryDto) {
        return this.categoryService.create(categoryDto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.categoryService.delete(id);
    }
}