package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.net.InternetDomainName;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class ScalarTypesTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testInternetDomainNameSerialization() throws Exception
    {
        final String INPUT = "google.com";
        InternetDomainName name = InternetDomainName.from(INPUT);
        assertEquals(q(INPUT), MAPPER.writeValueAsString(name));
    }

    @Test
    public void testInternetDomainNameDeserialization() throws Exception
    {
        final String INPUT = "google.com";
//        InternetDomainName name = MAPPER.readValue(quote(INPUT), InternetDomainName.class);
        InternetDomainName name = new ObjectMapper().readValue(q(INPUT), InternetDomainName.class);
        assertNotNull(name);
        assertEquals(INPUT, name.toString());
    }
}
