package com.ghost.client.util;

import com.ghost.client.GhostClient;
import com.ghost.client.module.Module;
import com.ghost.client.module.config.Setting;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {
    private final Path path;

    public ConfigManager() {
        path = FabricLoader.getInstance().getConfigDir().resolve("ghost-client.json");
    }

    public void save() {
        StringBuilder sb=new StringBuilder("{\n");
        List<Module> mods=GhostClient.getModuleManager().getModules();
        for (int i=0;i<mods.size();i++) {
            Module m=mods.get(i);
            sb.append("  \"").append(m.getName()).append("\": {\n");
            sb.append("    \"enabled\": ").append(m.isEnabled());
            for (Setting<?> s:m.getSettings()) {
                Object v=s.getValue();
                sb.append(",\n    \"").append(s.getName()).append("\": ");
                if (v instanceof Boolean||v instanceof Number) sb.append(v);
                else sb.append("\"").append(v).append("\"");
            }
            sb.append("\n  }");
            if (i<mods.size()-1) sb.append(",");
            sb.append("\n");
        }
        sb.append("}\n");
        try { Files.writeString(path,sb); } catch (IOException e) { GhostClient.LOGGER.error("[Ghost] Save failed: {}",e.getMessage()); }
    }

    public void load() {
        if (!Files.exists(path)) return;
        try {
            String raw=Files.readString(path);
            for (Module m:GhostClient.getModuleManager().getModules()) {
                String block=block(raw,m.getName());
                if (block==null) continue;
                Boolean en=bool(block,"enabled"); if(en!=null) m.setEnabled(en);
                for (Setting<?> s:m.getSettings()) {
                    if (s.getValue() instanceof Boolean){Boolean b=bool(block,s.getName());if(b!=null)s.setValueRaw(b);}
                    else if (s.getValue() instanceof Number){Double d=num(block,s.getName());if(d!=null)s.setValueRaw(d);}
                }
            }
        } catch (IOException e) { GhostClient.LOGGER.error("[Ghost] Load failed: {}",e.getMessage()); }
    }

    private String block(String j,String k) {
        int ki=j.indexOf("\""+k+"\""); if(ki==-1) return null;
        int o=j.indexOf('{',ki); if(o==-1) return null;
        int dep=0,i=o;
        while(i<j.length()){char c=j.charAt(i);if(c=='{')dep++;else if(c=='}'){dep--;if(dep==0)return j.substring(o,i+1);}i++;}
        return null;
    }

    private Boolean bool(String b,String k) {
        int ki=b.indexOf("\""+k+"\""); if(ki==-1) return null;
        int c=b.indexOf(':',ki); if(c==-1) return null;
        String r=b.substring(c+1).trim();
        if(r.startsWith("true")) return true; if(r.startsWith("false")) return false; return null;
    }

    private Double num(String b,String k) {
        int ki=b.indexOf("\""+k+"\""); if(ki==-1) return null;
        int c=b.indexOf(':',ki); if(c==-1) return null;
        String r=b.substring(c+1).trim(); StringBuilder n=new StringBuilder();
        for(char c2:r.toCharArray()){if(Character.isDigit(c2)||c2=='.'||c2=='-')n.append(c2);else break;}
        try{return Double.parseDouble(n.toString());}catch(NumberFormatException e){return null;}
    }
}
