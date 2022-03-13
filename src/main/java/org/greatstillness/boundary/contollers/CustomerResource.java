package org.greatstillness.boundary.contollers;

import org.greatstillness.control.dtos.CustomerDto;
import org.greatstillness.control.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

@Path("/customers")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @GET
    public List<CustomerDto> findAll() {
        return this.customerService.findAll();
    }

    @GET
    @Path("/{id}")
    public CustomerDto findById(@PathParam("id") Long id) {
        return this.customerService.findById(id);
    }

    @GET
    @Path("/active")
    public List<CustomerDto> findAllActive() {
        return this.customerService.findAllActive();
    }

    @GET
    @Path("/inactive")
    public List<CustomerDto> findAllInactive() {
        return this.customerService.findAllInactive();
    }

    @POST
    public CustomerDto create(CustomerDto customerDto) {
        return this.customerService.create(customerDto);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        this.customerService.delete(id);
    }
}