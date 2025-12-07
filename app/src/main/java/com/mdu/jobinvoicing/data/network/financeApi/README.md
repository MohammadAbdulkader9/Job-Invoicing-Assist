## Example Initialization

note: This should only be done once per application lifecycle.

```java
ApiServiceManager.createInstance(ApiServiceManager.ApiProvider.FORTNOX);
```

## Example Usage

The function **onSuccess(...)** needs to be implemented to handle the successful response from the API call. **onError(...)** does not need to be implemented if you do not want a custom handler for errors.

**onError(...)** or **onFailure(...)** are only invoked when the API call fails. That means that if the API call is successful, the **onSuccess(...)** function will be invoked, even if the response is not successful.

```java
ApiServiceManager asm = ApiServiceManager.getInstance();
asm.getInvoiceById("4", new ApiCallback<InvoiceResponse>() {
    @Override
    public void onSuccess(InvoiceResponse response) {
        System.out.println(response.getAddress1());
    }
    @Override
    public void onError(String errorMessage){
        System.out.println("this is a custom error callback function");
    }
});
```

## Useful Retrofit methods

### isSuccessful()

```java
ApiServiceManager asm = ApiServiceManager.getInstance();
asm.getInvoiceById("4", new ApiCallback<InvoiceResponse>() {
    @Override
    public void onSuccess(InvoiceResponse response) {
        if(response.isSuccessful()){
            System.out.println(response.getAddress1());
        } else {...}
    }
});
```

## API Service Manager

The api Service Manager is responsible for managing the API service instance and provides a unified interface for interacting with different API providers.

## Response Classes

Each class has a accompanying set of API calls that can be used to retrieve data from the API. e.g.

- **InvoiceResponse**
  -- **getInvoiceById(String id, ApiCallback<InvoiceResponse> callback)**
- **CustomerResponse**
  -- **getCustomerById(String id, ApiCallback<CustomerResponse> callback)**
- . . .

Each field in the classes is serialized and should cover every field for every api provider.

## Service Interface

The service interface is used to interact with a specific API. It provides a set of methods that can be used to retrieve data from the API.
