package xyz.staffjoy.faraday.core.mappings;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import xyz.staffjoy.faraday.config.FaradayProperties;
import xyz.staffjoy.faraday.config.MappingProperties;
import xyz.staffjoy.faraday.core.http.HttpClientProvider;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

public abstract class MappingsProvider {

    private static final ILogger log = SLoggerFactory.getLogger(MappingsProvider.class);

    protected final ServerProperties serverProperties;
    protected final FaradayProperties faradayProperties;
    protected final MappingsValidator mappingsValidator;
    protected final HttpClientProvider httpClientProvider;
    protected List<MappingProperties> mappings;

    public MappingsProvider(
            ServerProperties serverProperties,
            FaradayProperties faradayProperties,
            MappingsValidator mappingsValidator,
            HttpClientProvider httpClientProvider
    ) {
        this.serverProperties = serverProperties;
        this.faradayProperties = faradayProperties;
        this.mappingsValidator = mappingsValidator;
        this.httpClientProvider = httpClientProvider;
    }

    public MappingProperties resolveMapping(String originHost, HttpServletRequest request) {
        if (shouldUpdateMappings(request)) {
            updateMappings();
        }
        List<MappingProperties> resolvedMappings = mappings.stream()
                .filter(mapping -> originHost.toLowerCase().equals(mapping.getHost().toLowerCase()))
                .collect(Collectors.toList());
        if (isEmpty(resolvedMappings)) {
            return null;
        }
        return resolvedMappings.get(0);
    }

    @PostConstruct
    protected synchronized void updateMappings() {
        /*
        生成路由映射表
         */
        List<MappingProperties> newMappings = retrieveMappings();
        /*
        校验路由映射表
         */
        mappingsValidator.validate(newMappings);
        /*
        保存校验路由映射表
         */
        mappings = newMappings;
        /*
        更新HttpClient映射表
        HttpClient映射表 是通过 路由映射表 计算出来的
        更新 路由映射表 时候 也要更新 HttpClient映射表
         */
        httpClientProvider.updateHttpClients(mappings);

        log.info("Destination mappings updated", mappings);
    }

    /*
    应不应该更新 路由映射表
     */
    protected abstract boolean shouldUpdateMappings(HttpServletRequest request);
    /*
    获取 新的 路由映射表
     */
    protected abstract List<MappingProperties> retrieveMappings();
}
