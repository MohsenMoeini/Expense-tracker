package ir.snp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpensesAppTest {
    @MockitoBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setup(){
        Jwt mockJwt = Jwt.withTokenValue("test-token")
                .headers(stringObjectMap -> stringObjectMap.put("Alg","123"))
                .claim("preferred-username", "test-username")
                .build();
        when(jwtDecoder.decode(any())).thenReturn(mockJwt);
    }
    @Test
    void contextLoads(){
    }

}