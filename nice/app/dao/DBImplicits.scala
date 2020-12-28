package dao

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

@Singleton
class DBImplicits @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
    dbConfigProvider.get[JdbcProfile].db.run(databaseOperation)
  }
}
