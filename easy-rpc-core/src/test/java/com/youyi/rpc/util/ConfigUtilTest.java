package com.youyi.rpc.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.youyi.rpc.config.Config;
import com.youyi.rpc.constants.RpcConstant;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class ConfigUtilTest {

    @Test
    void load() {
        Config config = ConfigUtil.load(Config.class, "rpc", "test");
        log.info("test config: {}", config);
    }

    @Test
    void loadYaml() {
        String path = "conf/conf.yml";
        Map<String, Object> props = YamlUtil.loadByPath(path);
        log.info("path: {}, props: {}", path, props);

        JSONObject jsonObj = JSONUtil.parseObj(props);
        JSONObject rpcConfigProps = jsonObj.getJSONObject("rpc");
        Config config = JSONUtil.toBean(rpcConfigProps, Config.class);
        log.info("config from yaml: {}", config);
    }

    @Test
    void loadConfig() {
        Config config = ConfigUtil.load(Config.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        log.info("config: {}", config);
    }
}