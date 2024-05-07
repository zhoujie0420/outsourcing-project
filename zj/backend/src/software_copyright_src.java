package com.gra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: elk
 * @create: 2024-02-07 09:27
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordAddDto {
    String userId1;
    String userId2;
}
package com.gra.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: elk
 * @create: 2024-02-01 15:02
 **/
@Data
public class UserList {

    private List<String> userList;
    private String userRole;
}
package com.gra.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: elk
 * @create: 2024-01-19 10:34
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class token {
    private String token;
}
package com.gra.backend.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: elk
 * @create: 2024-02-01 16:27
 **/
@Data
public class UserInfoRep {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String phone;
    /**
     * 身份 医生 2 患者 1
     */
    private Integer role;
    private Integer roleId;

    private String departmentName;
}
package com.gra.backend.config;

import com.gra.backend.config.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }  //加密算法

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 放行这两个接口
                .antMatchers(
                        "/api/user/account/getRole/",
                        "/api/user/account/token/",
                        "/api/user/account/register/",
                        "/actions",
                        "/record/test",
                        "/oss"
                ).permitAll()
                .antMatchers("/pk/game/snake/start", "/pk/game/snake/receiveBot").hasIpAddress("127.0.0.1")   // 增加此行、
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                .antMatchers("/profile/**").anonymous()
                .antMatchers("/common/download**").anonymous()
                .antMatchers("/common/download/resource**").anonymous()
                .antMatchers("/swagger-ui.html").anonymous()
                .antMatchers("/swagger-resources/**").anonymous()
                .antMatchers("/webjars/**").anonymous()
                .antMatchers("/*/api-docs").anonymous()
                .antMatchers("/druid/**").anonymous()
                .antMatchers("/druid/**").anonymous()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/websocket/**")
                .antMatchers("/peerServerEndpoint/**");
    }


}package com.gra.backend.config.filter;

import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.User;
import com.gra.backend.service.utils.UserDetailsImpl;
import com.gra.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,  HttpServletResponse response,  FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user = userMapper.selectById(Integer.parseInt(userid));

        if (user == null) {
            throw new RuntimeException("用户名未登录");
        }

        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}package com.gra.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
package com.gra.backend.config;


//配置RestTemplate
//向MatchingSystem发请求，需要借助Springboot中的一个工具RestTemplate，它可以在两个Spring进程之间进行通信。
//
//先配置一下这个工具，如果希望在WebSocketServer.java中使用RestTemplate，就需要加Bean注解，这样才能够取出来。
//
//可以理解为，需要用到某个工具的时候，就定义一个它的Configuration，加一个注解Bean，返回一个它的实例。

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
package com.gra.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class ServerEndpointExporterConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}package com.gra.backend.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//解决springboot跨域请求的问题
@Configuration
class CorsConfig implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        if(origin!=null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        String headers = request.getHeader("Access-Control-Request-Headers");
        if(headers!=null) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
}package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Appointment;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends MPJBaseMapper<Appointment> {
}package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Doctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: elk
 * @create: 2024-02-05 17:49
 **/
@Mapper
public interface PatientMapper extends MPJBaseMapper<PatientMapper> {
}
package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Doctor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorMapper extends MPJBaseMapper<Doctor> {
}
package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Department;
import com.gra.backend.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends MPJBaseMapper<Department> {
}
package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MPJBaseMapper<User> {

}
package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: elk
 * @create: 2024-02-05 16:19
 **/

@Mapper
public interface RecordMapper extends MPJBaseMapper<Record> {

}
package com.gra.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;  // 有效期14天
    public static final String JWT_KEY = "JSDFSDFSDFASJDHASDFADSDFSDFSE234234235234523FGSDGSDFSDFSDFSDG5367123";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);
    }

    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}package com.gra.backend.utils.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *@Description: redis工具类
 *@Author: wl 2020/3/13 11：20
 */
@Configuration
public class RedisConfig {
    /**
     * 实例化 RedisTemplate 对象
     * 提供给 RedisUtil 使用
     * @param redisConnectionFactory springboot配置好的连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> RedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        initRedisTemplate(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }
    /**
     * 设置数据存入 redis 的序列化方式,并开启事务
     *
     * @param redisTemplate
     * @param factory
     * @autor wl
     * @date  2020/3/13 11：20
     */
    private void initRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory) {
        //如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast to String！
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 开启事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
    }

    /**
     * 注入封装RedisTemplate 给RedisUtil提供操作类
     * @param redisTemplate
     * @return RedisUtil
     * @autor wl
     * @date 2020/3/13 11：20
     */
    @Bean(name = "redisUtil")
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        return redisUtil;
    }
}

package com.gra.backend.utils.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description: redis工具类
 * @Author: wl 2020/3/13 10:47
 */
public class RedisUtil {

    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    //=============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 获取所有的key
     */
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }
    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

package com.gra.backend.utils.constants;


/**
 * @ClassName : RedisConstants  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/5/24  15:30
 */

public class RedisConstants {

    public static final String CACHE_RANK_LIST = "cache_rank_list";
}
package com.gra.backend.utils.rabbitmq;

import org.springframework.stereotype.Component;

@Component
public class Consumer {
    /**
     * @param message 参数message为消息的内容
     */

    public void consume(String message) {
        System.out.println("rabbitmq" + message);
    }

}
package com.gra.backend.utils.rabbitmq;

public class Producer {
}
package com.gra.backend.utils;


import java.util.regex.Pattern;

/**
 * @author lele
 * @create 2022-09-03 20:25
 */
public class RegularUtil {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(17[7])|(18[0,0-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    public static final String REGEX_AVATAR_URL = "(https?:[^:<>\"]*\\/)([^:<>\"]*)(\\.((png!thumbnail)|(png)|(jpg)|(jpeg)|(webp)))";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    public static boolean isAvatarUrl(String url) {
        return Pattern.matches(REGEX_AVATAR_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }
}
package com.gra.backend.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName : OssUtil  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/5/21  20:09
 */
@Component
public class OssUtil implements InitializingBean {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyID;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accesskeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = accessKeyID;
        ACCESS_KEY_SECRET = accesskeySecret;
        BUCKET_NAME = bucketName;
    }
}package com.gra.backend.utils;

import com.gra.backend.pojo.User;
import com.gra.backend.service.utils.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author lele
 * @create 2022-08-31 14:56
 */
public class UserUtil {

    public static User getUser() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        return loginUser.getUser();
    }
}
package com.gra.backend.controller.utils;

import com.gra.backend.service.utils.UtilsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/utils")
public class UtilsController {

    private final UtilsService utilsService;

    @GetMapping("email")
    public Map<String,String> sendEmail(@RequestParam Map<String,String> map){
        String email = map.get("email");
        return utilsService.sendEmail(email);
    }

}
package com.gra.backend.controller;

import com.gra.backend.common.result.Result;
import com.gra.backend.dto.RecordAddDto;
import com.gra.backend.pojo.Record;
import com.gra.backend.service.RecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author: jiezhou
 **/

@RestController
@RequestMapping("/api/record")
public class RecordController {
    @Resource
    private RecordService recordService;

    @PostMapping("addRecord")
    public Result<?> addRecord(RecordAddDto recordAddDto) {
        return recordService.addRecord(recordAddDto);
    }

    @PostMapping("getRecords")
    public Result<?> getRecords() {
        return recordService.getRecords();
    }

    @PostMapping("updateRecord")
    public Result<?> updateRecord(Record record) {
        return recordService.updateRecord(record);
    }
}
package com.gra.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: jiezhou
 **/
@RestController
public class ActionsController {
    @GetMapping("/actions")
    public String actions() {
        return "GitHub Actions";
    }

}package com.gra.backend.controller;

import com.gra.backend.common.result.Result;
import com.gra.backend.dto.UserList;
import com.gra.backend.pojo.User;
import com.gra.backend.service.AccountService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
@RestController
@AllArgsConstructor
@RequestMapping("/api/user/account")

public class AccountController {

    private final AccountService accountService;

    @GetMapping("test")
    public Result<?> test() {
        return Result.success("test");
    }
    @PostMapping("token") //账号密码获取token
    public Result<?> getToken(User user) {
        return accountService.getToken(user);
    }

    @GetMapping("info")
    public Result<?> getInfo() {
        return accountService.getInfo();
    }

    @PostMapping("register")
    public Result<?> register(User user) {
        return accountService.register(user);
    }

    @PostMapping("getRole")
    public Result<?> getRole(UserList userList) {
        return accountService.getRole(userList);
    }


}
package com.gra.backend.controller;/**
 * @author zhouj
 * @create 2023/6/10 17:37
 * @ClassName : OssController  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/10  17:37
 * @ClassName : OssController  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/10  17:37
 * @ClassName : OssController  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/10  17:37
 */

/**
 * @ClassName : OssController  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/10  17:37
 */


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Oss相关操作接口
 */
@RestController
public class OssController {
    @GetMapping("/oss")
    public String oss() {
        String url = null;
        return "oss";
    }

    @PostMapping("/upload")
    public String upload(@RequestPart("file") MultipartFile file) {
        String url = null;
        return url;
    }


    @PostMapping("/uploadImages")
    public String uploadImages(@RequestPart("file") MultipartFile file) {
        // 获取上传头像的文件 MultipartFile
        // 返回上传的oss路径
        String url = null;
        return url;
    }


}package com.gra.backend.common.result;

/**
 * @author: elk
 * @create: 2024-01-19 10:18
 **/


import com.gra.backend.common.constant.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    private boolean status;


    private Result(ResultCode resultCode, T data, boolean status) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.status = status;
    }

    /**
     * 无数据成功返回
     *
     * @return
     */
    public static <T> Result success() {
        return new Result<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, true);
    }

    /**
     * 带数据返回
     */
    public static <T> Result success(T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, true);
    }

    /**
     * 失败
     */
    public static <T> Result fail() {
        return new Result<T>(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage(), null, false);
    }

    public static <T> Result fail(String message) {
        return new Result<T>(ResultCode.FAIL.getCode(), message, null, false);
    }

    /**
     * 失败
     */
    public static <T> Result fail(T data) {
        return new Result<T>(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage(), data, false);
    }


    @Override
    public String toString() {
        return "ResultUtils [code=" + code + ", message=" + message + ", data=" + data + "]";
    }
}
package com.gra.backend.common.constant;

/**
 * @author: elk
 * @create: 2024-02-01 16:32
 **/

public class UserRoleConstant {
    /**
     * 医生
     */
    public static final Integer DOCTOR_CODE = 2;
    /**
     * 患者
     */
    public static final Integer PATIENT_CODE = 1;
}
package com.gra.backend.common.constant;

/**
 * @author: elk
 * @create: 2024-01-19 10:21
 **/

import lombok.Getter;


@Getter
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "成功"),
    FAIL(1000, "失败"),
    FAILED(400, "请求失败"),
    NOT_FOUND(404, "未找到"),
    SERVER_ERROR(500, " 服务器内部出错 "),
    /**
     * 错误参数
     */
    PARAM_IS_INVALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    /**
     * 用户错误
     */
    USER_NOT_LOGIN_IN(2001, "用户未登录"),
    USER_LOGIN_ERROR(2002, "账号不存在或者密码错误"),
    USER_ACCOUNT_FORBIDDEN(2003, "账户被禁用"),
    USER_NOT_EXISTS(2004, "用户不存在"),
    USER_HAS_EXISTED(2005, "用户已存在");

    /**
     * 代码
     */
    private final Integer code;
    /**
     * 信息
     */
    private final String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
package com.gra.backend.consumer.utils;

import com.gra.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    public static Integer getUserId(String token) {
        int userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userId;
    }
}
package com.gra.backend.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yi Dai daiyi.lucky@qq.com
 * @since 2023/8/8 14:40
 */

@Slf4j
@Component
@ServerEndpoint("/peerServerEndpoint/{peerId}")
public class PeerServerEndpoint {

    private static final Map<String, Session> peerIdSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("peerId") String peerId) {
        log.info("on open:the session is is :{},the peer id is:{}", session.getId(), peerId);
        Session removedSession = PeerServerEndpoint.peerIdSessionMap.remove(peerId);
        try {
            if (Objects.nonNull(removedSession)) {
                removedSession.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            PeerServerEndpoint.peerIdSessionMap.put(peerId, session);
            refreshOnlineSessionsList();
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("peerId") String peerId) {
        log.warn("on close:the session is is :{},the peer id is:{}", session.getId(), peerId);
        Session removedSession = PeerServerEndpoint.peerIdSessionMap.remove(peerId);
        try {
            if (Objects.nonNull(removedSession)) {
                removedSession.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            refreshOnlineSessionsList();
        }
    }

    @OnError
    public void onError(Session session, Throwable e, @PathParam("peerId") String peerId) {
        log.error("on error:the session is is :{},the exception class is: {},the peer id is:{}", session.getId(), e.getClass(), peerId);
        onClose(session, peerId);
        e.printStackTrace();
    }

    private void refreshOnlineSessionsList() {
        PeerServerEndpoint.peerIdSessionMap.forEach((key, value) -> {
            value.getAsyncRemote().sendText(JSON.toJSONString(PeerServerEndpoint.peerIdSessionMap.keySet()));
        });
    }

}
package com.gra.backend.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.UserList;
import com.gra.backend.dto.token;
import com.gra.backend.mapper.DepartmentMapper;
import com.gra.backend.mapper.DoctorMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Department;
import com.gra.backend.pojo.Doctor;
import com.gra.backend.pojo.User;
import com.gra.backend.response.UserInfoRep;
import com.gra.backend.service.utils.UserDetailsImpl;
import com.gra.backend.utils.JwtUtil;
import com.gra.backend.utils.UserUtil;
import com.gra.backend.utils.redis.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gra.backend.common.constant.ResultCode.USER_HAS_EXISTED;
import static com.gra.backend.common.constant.UserRoleConstant.PATIENT_CODE;


@Service
@AllArgsConstructor
public class AccountService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private AuthenticationManager authenticationManager;


    public Result<?> getToken(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken); //登录失败会自己处理
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User loginuser = loginUser.getUser(); // 取出user
        String jwt = JwtUtil.createJWT(loginuser.getId().toString());

        return Result.success(new token(jwt));
    }

    public Result<?> getInfo() {
        User user = UserUtil.getUser();
        user.setPassword(null);
        return Result.success(user);
    }

    public Result<?> register(User user) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        if (!users.isEmpty()) {
            return Result.fail(USER_HAS_EXISTED.getMessage());
        }
        userMapper.insert(user);
        return Result.success();
    }

    public Result<?> getRole(UserList userList) {
        Integer role = Integer.valueOf(userList.getUserRole());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // string list 转 int list stream
        if (!(userList.getUserList() == null)){
            List<Integer> list = Arrays.stream(userList.getUserList().toArray())
                    .map(Object::toString)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            userLambdaQueryWrapper.in(User::getId, list);
        }
        userLambdaQueryWrapper.ne(User::getRole, role);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        List<UserInfoRep> res = new ArrayList<>();
        if (Objects.equals(role, PATIENT_CODE)) {
            users.forEach(
                    nowUser -> {
                        UserInfoRep userInfoRep = new UserInfoRep();
                        BeanUtil.copyProperties(nowUser, userInfoRep);
                        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        doctorLambdaQueryWrapper.eq(Doctor::getId, userInfoRep.getRoleId());
                        Doctor doctor = doctorMapper.selectOne(doctorLambdaQueryWrapper);
                        Integer departmentId = doctor.getDepartmentId();
                        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        departmentLambdaQueryWrapper.eq(Department::getId, departmentId);
                        Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
                        userInfoRep.setDepartmentName(department.getDepartmentName());
                        res.add(userInfoRep);
                    }
            );
        } else {
            users.forEach(
                    nowUser -> {
                        UserInfoRep userInfoRep = new UserInfoRep();
                        BeanUtil.copyProperties(nowUser, userInfoRep);
                        res.add(userInfoRep);
                    }
            );
        }
        return Result.success(res);
    }
}



//    public Result<?> getEmailToken(String email, String code) { //邮箱登录
//        Map<String, String> map = new HashMap<>();
//        if (!redisUtil.hasKey(email)) {
//            System.out.println("不存在这个email");
//            map.put("error_message", "邮箱错误");
//            return map;
//        }
//        // 强制转化object会报  java.lang.Integer cannot be cast to java.lang.String 错误
//        // String.valueOf 实质调用 object.toString() 方法
//        String getcode = String.valueOf(redisUtil.get(email));
//        System.out.println(getcode);
//        System.out.println(code);
//        if (!Objects.equals(code, getcode)) {
//            map.put("error_message", "验证码错误");
//            return map;
//        }
//        //下面是成功匹配后的操作
//        //需要将 redis 的 key 删除
//        redisUtil.del(email);
//
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("email", email);
//        User user = userMapper.selectOne(queryWrapper);
//
//        //生成jwt (通过id)
//        String jwt = JwtUtil.createJWT(user.getId().toString());
//
//        map.put("error_message", "success");
//        map.put("token", jwt);
//        return map;
//    }
package com.gra.backend.service.utils;


import com.gra.backend.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UtilsService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private RedisUtil redisUtil;
    public Map<String,String> sendEmail(String email){
        Date now = new Date();
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000)); // 随机一个 4位长度的验证码
        System.out.println(code);

        //存入redis中，key为email ,value 为code ,时间为1min
        redisUtil.set(email,code,60);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("zzac0420@163.com");  // 发送人
        message.setTo(email);
        message.setSentDate(now);
        message.setSubject("【ZZAC Of Game】登录邮箱验证");
        message.setText("您本次登录的验证码是：" + code + "，有效期1分钟");
        javaMailSender.send(message);
        Map<String,String> map = new HashMap<>();
        map.put("error_message" ,"success");
        return map;

    }


}
package com.gra.backend.service.utils;

import com.gra.backend.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
package com.gra.backend.service;
/**
 * @author zhouj
 * @create 2023/6/10 17:35
 */

import org.springframework.web.multipart.MultipartFile;

/**
 * oss上传管理Service
 */
public interface IOssService {
    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    String uploadImages(MultipartFile file);

    /**
     * 删除图片
     *
     * @param fileUrl
     * @return
     */
    boolean deleteImages(String fileUrl);


    String uploadFile(MultipartFile file);
}package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.RecordAddDto;
import com.gra.backend.mapper.RecordMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Record;
import com.gra.backend.pojo.User;
import com.gra.backend.utils.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiezhou
 **/

@Service
public class RecordService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RecordMapper recordMapper;


    public Result<?> addRecord(RecordAddDto recordAddDto) {
        Record record = new Record();
        //判断userId是医生还是患者
        User user = userMapper.selectById(recordAddDto.getUserId1());
        if (user == null) {
            return Result.fail("用户不存在");
        }
        if (user.getRole() == 2) {
            record.setDoctorId(Integer.valueOf(recordAddDto.getUserId1()));
            record.setPatientId(Integer.valueOf(recordAddDto.getUserId2()));
        } else if (user.getRole() == 1) {
            record.setDoctorId(Integer.valueOf(recordAddDto.getUserId2()));
            record.setPatientId(Integer.valueOf(recordAddDto.getUserId1()));
        }
        record.setStatus(0);
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        String formattedDateTime = now.format(formatter);
        record.setConsultationDate(formattedDateTime);
        recordMapper.insert(record);
        return Result.success();
    }

    public Result<?> getRecords() {
        User user = UserUtil.getUser();
        if (user == null) {
            return Result.fail("用户未登录");
        }
        LambdaQueryWrapper<Record> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer role = user.getRole();
        recordLambdaQueryWrapper.orderByDesc(Record::getConsultationDate);
        List<Record> records = new ArrayList<>();
        if (role == 2) {
            recordLambdaQueryWrapper.eq(Record::getDoctorId, user.getId());
            records = recordMapper.selectList(recordLambdaQueryWrapper);
            for (Record record : records) {
                User patient = userMapper.selectById(record.getPatientId());
                record.setOtherName(patient.getUsername());
            }
        } else if (role == 1) {
            recordLambdaQueryWrapper.eq(Record::getPatientId, user.getId());
            records = recordMapper.selectList(recordLambdaQueryWrapper);
            for (Record record : records) {
                User doctor = userMapper.selectById(record.getDoctorId());
                record.setOtherName(doctor.getUsername());
            }
        }

        return Result.success(records);
    }

    public Result<?> updateRecord(Record record) {
        record.setStatus(1);
        recordMapper.updateById(record);
        return Result.success();
    }
}
package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.User;
import com.gra.backend.service.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    // 传入 username 返回对应的信息，在这里也就是id name pwd
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return new UserDetailsImpl(user);
    }
}package com.gra.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author: jiezhou
 * 预约表
 * 设计：
 * 1.预约表中的doctorId和patientId都是外键，分别指向医生表和患者表
 * 2.预约表中的appointmentDate是预约日期，timeSlot是预约时间批次，1 代表 9点到 9.30 2 代表 9.30 到 10点 以此类推
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer doctorId;
    private Integer patientId;
    private Date appointmentDate;
    /**
     * 预约时间批次
     */
    private Integer timeSlot;
    private Integer status;
}
package com.gra.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: elk
 * @create: 2024-01-19 09:53
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {
    private Integer id;
    private String departmentName;
    private String description;
}
package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String phone;
    /**
     * 身份 医生 2 患者 1
     */
    private Integer role;
    private Integer roleId;
}
package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: jiezhou
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer departmentId;
    private String recordList;
}package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: jiezhou
 * 问诊结果
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer doctorId;
    private Integer patientId;
    /**
     * 会诊日期
     */
    private String consultationDate;
    /**
     * 诊断结果
     */
    private String diagnosis;
    /**
     * 处方
     */
    private String prescription;
    /**
     * videoUrl
     */
    private String videoUrl;
    // 0 未完成 1 已完成
    private Integer status;

    //返回的都是对方的name
    @TableField(exist = false)
    private String otherName;

}
package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: elk
 * @create: 2024-01-19 09:48
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @TableId(type = IdType.AUTO)
    private Integer id;


    // ,隔开
    private String recordList;


}
