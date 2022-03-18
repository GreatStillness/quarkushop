package org.greatstillness.boundary.contollers;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.greatstillness.control.dtos.ReviewDto;
import org.greatstillness.control.services.ReviewService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("/reviews")
@Tag(name = "reviews", description = "All the reviews methods")
public class ReviewResource {

    @Inject
    ReviewService reviewService;

    @GET
    @Path("/product/{id}")
    public List<ReviewDto> findAllByProduct(@PathParam("id") Long id) {
        return this.reviewService.findReviewsByProductId(id);
    }

    @POST
    @Path("/product/{id}")
    public ReviewDto create(ReviewDto reviewDto, @PathParam("id") Long id) {
        return this.reviewService.create(reviewDto, id);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.reviewService.delete(id);
    }
}