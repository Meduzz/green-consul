package se.chimps.green.discovery.consul

import java.util.UUID

import org.scalatest.FunSuite

class ConsulDiscoveryTest extends FunSuite {

	test("Oh hai der") {
		val subject = new ConsulDiscovery
		val r = Runtime.getRuntime

		val id = UUID.randomUUID().toString
		val service = "GreenTest"
		val host = "127.0.0.2"
		val port = 65000

		try {
			subject.register(id, service, host, port)
		} catch {
			case e:Throwable => fail(e.getMessage)
		}

		try {
			val found = subject.lookup(service)

			if (found.isEmpty) {
				fail("Service was not found.")
			} else if (found.size > 1) {
				fail("There are more than 1 node registered")
			} else {
				val node1 = found(0)

				assert(node1._1 == host)
				assert(node1._2 == port)
			}
		} catch {
			case e:Throwable => fail(e.getMessage)
		}

		try {
			subject.deregister(id)
		} catch {
			case e:Throwable => fail(e.getMessage)
		}

		try {
			val found = subject.lookup(service)

			if (found.nonEmpty) {
				fail("The node was most likely not removed")
			}
		} catch {
			case e:Throwable => fail(e.getMessage)
		}
	}
}
