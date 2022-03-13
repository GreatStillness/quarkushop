package org.greatstillness.boundary.contollers;

import org.greatstillness.control.dtos.PaymentDto;
import org.greatstillness.control.services.PaymentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("/payments")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @GET
    public List<PaymentDto> findAll() {
        return this.paymentService.findAll();
    }

    @GET
    @Path("/{id}")
    public PaymentDto findById(@PathParam("id") Long id) {
        return this.paymentService.findById(id);
    }

    @POST
    public PaymentDto create(PaymentDto paymentDto) {
        return this.paymentService.create(paymentDto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.paymentService.delete(id);
    }

    @GET
    @Path("/price/{max}")
    public List<PaymentDto> findPaymentsByAmountRangeMax(@PathParam("max") double max) {
        return this.paymentService.findByPriceRange(max);
    }
}