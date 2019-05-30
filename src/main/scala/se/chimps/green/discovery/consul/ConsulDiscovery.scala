package se.chimps.green.discovery.consul

import se.chimps.green.spi.Discovery
import se.kodiak.tools.yahc.Yahc._

class ConsulDiscovery extends Discovery {

	// GET /catalog/service/:service
	override def lookup(service:String):Seq[(String, Int)] = {
		val server = if (System.getenv().containsKey("CONSUL_SRV")) {
			System.getenv("CONSUL_SRV")
		} else {
			"http://localhost:8500"
		}

		val auth:Option[String] = if (System.getenv().containsKey("CONSUL_AUTH")) {
			Some(System.getenv("CONSUL_AUTH"))
		} else {
			None
		}

		val req = GET(url(server, s"catalog/service/$service"))

		val res = auth match {
			case Some(authz) => req.header("X-Consul-Token", authz).asString
			case None => req.asString
		}

		if (res.is2xx) {
			res.asJson[Seq[AvailableService]]
        .map(s => (s.ServiceAddress, s.ServicePort))
		} else {
			println(s"Lookup response code was not 200ish but ${res.code}.")
			println(res.body)
			Seq()
		}
	}

	// PUT /catalog/register
	override def register(id:String, service:String, ip:String, port:Int):Unit = {
		val server = if (System.getenv().containsKey("CONSUL_SRV")) {
			System.getenv("CONSUL_SRV")
		} else {
			"http://localhost:8500"
		}

		val auth:Option[String] = if (System.getenv().containsKey("CONSUL_AUTH")) {
			Some(System.getenv("CONSUL_AUTH"))
		} else {
			None
		}

		val cmd = Service(ip, port, id, service)
		val req = PUT(url(server, s"agent/service/register"), cmd)

		val res = auth match {
			case Some(authz) => req.header("X-Consul-Token", authz).asString
			case None => req.asString
		}

		if (!res.is2xx) {
			println(s"Register response was not 200ish but ${res.code}.")
			println(res.body)
		}
	}

	// PUT /catalog/deregister
	override def deregister(id:String):Unit = {
		val server = if (System.getenv().containsKey("CONSUL_SRV")) {
			System.getenv("CONSUL_SRV")
		} else {
			"http://localhost:8500"
		}

		val auth:Option[String] = if (System.getenv().containsKey("CONSUL_AUTH")) {
			Some(System.getenv("CONSUL_AUTH"))
		} else {
			None
		}

		val req = PUT.text(url(server, s"agent/service/deregister/$id"), "")

		val res = auth match {
			case Some(authz) => req.header("X-Consul-Token", authz).asString
			case None => req.asString
		}

		if (!res.is2xx) {
			println(s"Deregister response was not 200ish but ${res.code}.")
			println(res.body)
		}
	}

	private def url(srv:String, path:String):String = s"$srv/v1/$path"
}

case class Service(Address:String, Port:Int, ID:String, Name:String)
case class AvailableService(ServiceAddress:String, ServicePort:Int)