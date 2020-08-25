package me.pggsnap.demos.webflux.controller;

import me.pggsnap.demos.webflux.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author pggsnap
 * @date 2020/4/13
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MessageSource messageSource;

    /**
     * curl -X GET 'http://127.0.0.1:8080/user/get/123'
     * @param username
     * @return
     */
    @GetMapping("/get/{username}")
    public Mono<User> username(@PathVariable String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setState(1);
        return Mono.just(user);
    }

    /**
     * @RequestParam 只支持 query parameters
     * curl -X GET 'http://127.0.0.1:8080/user/get?username=abc'
     * @param username
     * @return
     */
    @GetMapping("/get")
    public Mono<User> findByUser(@RequestParam("username") String username,
                                 @RequestParam(name = "state", required = false) Integer state) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(null);
        user.setState(state);
        return Mono.just(user);
    }

    /**
     * @RequestParam 也支持 Map<String, String>
     * curl -X GET 'http://127.0.0.1:8080/user/getwithmap?username=abc&state=45&password=781'
     * @param map
     * @return
     */
    @GetMapping("/getwithmap")
    public Mono<User> findByMap(@RequestParam Map<String, String> map) {
        User user = new User();
        user.setUsername(map.get("username"));
        user.setPassword(map.get("password"));
        user.setState(Integer.valueOf(map.get("state")));
        return Mono.just(user);
    }

    /**
     * data bind 方式同时支持 query parameters, form data, multiparts
     * 同时支持
     *      curl -X GET 'http://127.0.0.1:8080/user/getform' -d 'username=abc&password=pass'
     *  以及
     *      curl -X GET 'http://127.0.0.1:8080/user/getform?username=aaa&&password=mn'
     * @param user
     * @return
     */
    @GetMapping("/getform")
    public Mono<User> form(User user) {
        return Mono.just(user);
    }

    /**
     * curl -L -X POST 'http://127.0.0.1:8080/user/add' -H 'Content-Type: application/json' -d '{"username": "abc", "password": "pass"}'
     * @param user
     * @return
     */
    @PostMapping("/add")
    public Mono<User> add(@RequestBody User user) {
//        System.out.println("user: " + user);
        return Mono.just(user);
    }

    /**
     * 单个文件上传
     * @param username
     * @param file
     * @return
     */
    @PostMapping(value = "/upload")
    public Mono<String> upload(@RequestParam(value = "username", required = false) String username,
                               @RequestPart FilePart file) {
        System.out.println("username: " + username);
        System.out.println("filename: " + file.filename());

        HttpHeaders httpHeaders = file.headers();
        for (Map.Entry entry : httpHeaders.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        File dest = new File("/Users/pggsnap/Downloads/cp" + file.filename());
        file.transferTo(dest);

        return Mono.just("ok");
    }

    @PostMapping(value = "/uploadasync")
    public Mono<String> uploadasync(@RequestParam(value = "username", required = false) String username,
                               @RequestPart Mono<FilePart> file) {
        System.out.println("username: " + username);
        return file.doOnSuccess(f -> {
            System.out.println("filename: " + f.filename());
            HttpHeaders httpHeaders = f.headers();
            for (Map.Entry entry : httpHeaders.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        })
                .thenReturn("ok");
    }

    /**
     * 批量文件上传
     * @param parts
     * @return
     */
    @PostMapping(value = "/multiupload")
    public Mono<String> multiUpload(@RequestBody Mono<MultiValueMap<String, Part>> parts) {
        return parts.doOnSuccess(stringPartMultiValueMap -> {
            for (Map.Entry<String, List<Part>> entry : stringPartMultiValueMap.entrySet()) {
                String key = entry.getKey();
                Part part = entry.getValue().get(0);
                part.content()
                        .reduce((db1, db2) -> db1.write(db2))
                        .filter(db -> db.capacity() < 10_000_000)
                        .map((db -> db.toString(Charset.forName("utf-8"))))
                        .subscribe(x -> System.out.println("file: " + key + "\ncontent: " + x));
            }
        }).thenReturn("ok");
    }
}
