package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests
{

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    protected WebApplicationContext wac;

    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Before
    public void setup()
    {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity()).build();
    }

    @Test
    public void testHome() throws Exception
    {
        this.mockMvc.perform(get("/")).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void viewName() throws Exception
    {
        this.mockMvc.perform(get("/home")).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testLoginMvc() throws Exception
    {
        RequestBuilder requestBuilder = formLogin().user("j_username", "admin")
                .password("j_password", "123456").loginProcessingUrl("/login-check");
        this.mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound())
                .andExpect(header().doesNotExist("my-remember-me"))
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    public void testLogin() throws Exception
    {
        HttpHeaders headers = getHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("j_username", "admin");
        form.set("j_password", "123456");
        form.set("remember-me", "true");
        ResponseEntity<String> entity = this.testRestTemplate.exchange("/login-check",
                HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(form, headers),
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(entity.getHeaders().getLocation().toString())
                .endsWith(this.port + "/home");
        assertThat(entity.getHeaders().get("Set-Cookie")).isNotNull();
        assertThat(entity.getHeaders().get("Set-Cookie").size()).isEqualTo(2);
        assertThat(entity.getHeaders().get("Set-Cookie").get(1))
                .startsWith("my-remember-me");
        ResponseEntity<String> page = this.testRestTemplate.exchange("/", HttpMethod.GET,
                new HttpEntity<Void>(headers), String.class);
        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testDenied() throws Exception
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("j_username", "user");
        form.set("j_password", "user");
        getCsrf(form, headers);
        ResponseEntity<String> entity = this.testRestTemplate.exchange("/login-check",
                HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(form, headers),
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        String cookie = entity.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        ResponseEntity<String> page = this.testRestTemplate.exchange(
                entity.getHeaders().getLocation(), HttpMethod.GET,
                new HttpEntity<Void>(headers), String.class);
        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testManagementProtected() throws Exception
    {
        ResponseEntity<String> entity = this.testRestTemplate.getForEntity("/beans",
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testManagementAuthorizedAccess() throws Exception
    {
        BasicAuthorizationInterceptor basicAuthInterceptor = new BasicAuthorizationInterceptor(
                "admin", "123456");
        this.testRestTemplate.getRestTemplate().getInterceptors()
                .add(basicAuthInterceptor);
        try
        {
            ResponseEntity<String> entity = this.testRestTemplate.getForEntity("/beans",
                    String.class);
            assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        finally
        {
            this.testRestTemplate.getRestTemplate().getInterceptors()
                    .remove(basicAuthInterceptor);
        }
    }

    @Test
    public void testManagementUnauthorizedAccess() throws Exception
    {
        BasicAuthorizationInterceptor basicAuthInterceptor = new BasicAuthorizationInterceptor(
                "user", "user");
        this.testRestTemplate.getRestTemplate().getInterceptors()
                .add(basicAuthInterceptor);
        try
        {
            ResponseEntity<String> entity = this.testRestTemplate.getForEntity("/beans",
                    String.class);
            assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
        finally
        {
            this.testRestTemplate.getRestTemplate().getInterceptors()
                    .remove(basicAuthInterceptor);
        }
    }

    @Test
    public void testCss() throws Exception
    {
        ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
                "/webjars/bootstrap/3.3.6/css/bootstrap.min.css", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).contains("body");
    }

    private HttpHeaders getHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> page = this.testRestTemplate.getForEntity("/login",
                String.class);
        assertThat(page.getStatusCode()).isEqualTo(HttpStatus.OK);
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        Pattern pattern = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
        Matcher matcher = pattern.matcher(page.getBody());
        assertThat(matcher.matches()).as(page.getBody()).isTrue();
        headers.set("X-CSRF-TOKEN", matcher.group(1));
        return headers;
    }

    private void getCsrf(MultiValueMap<String, String> form, HttpHeaders headers)
    {
        ResponseEntity<String> page = this.testRestTemplate.getForEntity("/login",
                String.class);
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        headers.set("Cookie", cookie);
        String body = page.getBody();
        Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*")
                .matcher(body);
        matcher.find();
        form.set("_csrf", matcher.group(1));
    }

}
