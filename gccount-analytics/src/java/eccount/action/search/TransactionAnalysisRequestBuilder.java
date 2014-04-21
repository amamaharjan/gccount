package eccount.action.search;

import eccount.ClientRequest;

import eccount.SearchRequest;
import eccount.action.AnalyticsRequestBuilders;
import eccount.util.DateUtils;
import eccount.util.FilterUtils;
import eccount.util.QueryUtils;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.search.facet.statistical.StatisticalFacetBuilder;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAnalysisRequestBuilder extends AnalyticsRequestBuilders.DefaultRequestBuilder {

    Logger logger = LoggerFactory.getLogger(TransactionAnalysisRequestBuilder.class.getName());

    public final String ESTYPE_CUSTOMER_SEARCH = "CustomerSearch";
    public final String ESTYPE_CUSTOMER        = "Customer";
    public final String ESTYPE_TRANSACTION     = "Transaction";

    public static String FIELD_BALANCE     = "balance";
    public static String BALANCE_FACETNAME = "balance_stats";

    @Override
    protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);

        SearchRequestBuilder paidAmountRequestBuilder  = preparePaidAmountStatisticalFacet(state, client);
        //multiSearchRequestBuilder.add(countRequestBuilder);
        multiSearchRequestBuilder.add(paidAmountRequestBuilder);
        System.out.println("paidAmountRequestBuilder = "+paidAmountRequestBuilder);
        return multiSearchRequestBuilder;
    }


    private SearchRequestBuilder prepareTermsStatsFacetsForCount(ClientRequest state, Client client) {
        SearchRequestBuilder searchRequestBuilder = prepareSearchRequestBuilder(state, client);
        addCustomerCountFacet(searchRequestBuilder, state);
        return searchRequestBuilder;
    }


    private SearchRequestBuilder preparePaidAmountStatisticalFacet(ClientRequest clientRequest, Client client) {
        SearchRequestBuilder dateRangeRequestBuilder = QueryUtils.prepareRequestBuilder(client,
                clientRequest.request,
                "reportingFrom",
                "reportingTo",
                clientRequest, 
                null,
                ESTYPE_CUSTOMER);       //ESTYPE_TRANSACTION
        StatisticalFacetBuilder transactionAmountFacet = FilterUtils.getStatisticalFacet(BALANCE_FACETNAME, FIELD_BALANCE, null);
        dateRangeRequestBuilder.addFacet(transactionAmountFacet);
        dateRangeRequestBuilder.addField(FIELD_BALANCE);
        return dateRangeRequestBuilder;
    }

    private void addCustomerCountFacet(SearchRequestBuilder builder, ClientRequest state) {
        builder.addFacet(termsStatsFacetBuilder(state, state.periodTo(), "customerCount"));
    }

    private SearchRequestBuilder prepareSearchRequestBuilder(ClientRequest state, Client esClient) {
        SearchRequest request = state.request;
        String index = request.hasParameter("clientId") ? request.get("clientId") : "XXX";

        SearchRequestBuilder builder = esClient.prepareSearch(index);
        builder.setSearchType(SearchType.COUNT);
        builder.setFrom(0).setSize(1).setExplain(false);
        builder.setTypes(ESTYPE_CUSTOMER_SEARCH);
        return builder;
    }


    private TermsStatsFacetBuilder termsStatsFacetBuilder(ClientRequest state, String month, String facetName) {
        Long longDate = DateUtils.getTimeFromDateWrtTimeZone(month);
        AndFilterBuilder andFilter = null;
        Long periodTo = DateUtils.getTimeFromDateWrtTimeZone(state.periodTo());
        return FilterUtils.getFacet(facetName, "searchKey", "valueField", andFilter);
    }
}

