package mcp.cloudtrace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

// contrived repository - probably not needed to illustrate tracing concerns.. maybe.
interface ClientRepository extends JpaRepository<Client, Long> {
    Collection<Client> findByClientId(String dn);
}
