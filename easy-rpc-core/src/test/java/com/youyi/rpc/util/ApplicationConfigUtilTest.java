package com.youyi.rpc.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.youyi.rpc.config.ApplicationConfig;
import com.youyi.rpc.constants.RpcConstants;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ApplicationConfigUtilTest {

    @Test
    void load() {
        ApplicationConfig applicationConfig = ConfigUtil.load(ApplicationConfig.class, "rpc", "test");
        log.info("test config: {}", applicationConfig);
    }

    @Test
    void loadYaml() {
        String path = "conf/conf.yml";
        Map<String, Object> props = YamlUtil.loadByPath(path);
        log.info("path: {}, props: {}", path, props);

        JSONObject jsonObj = JSONUtil.parseObj(props);
        JSONObject rpcConfigProps = jsonObj.getJSONObject("rpc");
        ApplicationConfig applicationConfig = JSONUtil.toBean(rpcConfigProps, ApplicationConfig.class);
        log.info("config from yaml: {}", applicationConfig);
    }

    @Test
    void loadConfig() {
        ApplicationConfig applicationConfig = ConfigUtil.load(ApplicationConfig.class, RpcConstants.DEFAULT_CONFIG_PREFIX);
        log.info("config: {}", applicationConfig);
    }
}