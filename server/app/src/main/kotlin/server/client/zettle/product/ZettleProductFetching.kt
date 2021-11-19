package server.client.zettle.product

interface ZettleProductFetching {

    suspend fun fetchProduct(accessToken: String): Result<ZettleProductResponse>
}
