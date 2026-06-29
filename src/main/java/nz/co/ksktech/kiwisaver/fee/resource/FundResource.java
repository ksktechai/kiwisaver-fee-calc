package nz.co.ksktech.kiwisaver.fee.resource;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nz.co.ksktech.kiwisaver.fee.model.CalculateRequest;
import nz.co.ksktech.kiwisaver.fee.model.CalculateResponse;
import nz.co.ksktech.kiwisaver.fee.model.Fund;
import nz.co.ksktech.kiwisaver.fee.repository.FundRepository;
import nz.co.ksktech.kiwisaver.fee.service.FeeCalculatorService;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FundResource {

    @Inject
    FundRepository fundRepository;

    @Inject
    FeeCalculatorService feeCalculatorService;

    @GET
    @Path("/funds")
    public List<Fund> listFunds() {
        Log.debugf(">>> GET /funds");
        List<Fund> funds = fundRepository.listAll();
        Log.debugf("<<< GET /funds -> %d funds", funds.size());
        return funds;
    }

    @GET
    @Path("/funds/{id}")
    public Fund getFund(@PathParam("id") String id) {
        Log.debugf(">>> GET /funds/%s", id);
        Fund fund = fundRepository.findById(id)
                .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
        Log.debugf("<<< GET /funds/%s -> %s", id, fund.name());
        return fund;
    }

    @POST
    @Path("/calculate")
    public CalculateResponse calculate(CalculateRequest request) {
        Log.debugf(">>> POST /calculate fundId=%s balance=%.2f years=%d",
                request.fundId(), request.balance(), request.years());
         Fund fund = fundRepository.findById(request.fundId())
                 .orElseThrow(() -> new WebApplicationException(
                         Response.status(Response.Status.NOT_FOUND).build()));
        CalculateResponse result = feeCalculatorService.calculate(fund, request.balance(),
                request.annualContribution(), request.years(), request.annualReturnPercent());
        Log.debugf("<<< POST /calculate -> projected=%.2f feesPaid=%.2f",
                result.projectedBalance(), result.totalFeesPaid());
        return result;
    }
}
