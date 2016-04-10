import javax.inject._

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

@Singleton
class Filters @Inject()(corsFilter: CORSFilter) extends HttpFilters {
  def filters = Seq(corsFilter)
}
