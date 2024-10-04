package graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import parsers.EclipseJDTASTParser;
import processors.ASTProcessor;

public abstract class CallGraph extends ASTProcessor {
   private Set<String> methods = new HashSet();
   private Map<String, Map<String, Integer>> invocations = new HashMap();
   private FileLogger loggerChain;

   public CallGraph(String projectPath) {
      super(projectPath);
      this.setLoggerChain();
   }

   protected void setLoggerChain() {
      this.loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
      this.loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
   }

   public Set<String> getMethods() {
      return this.methods;
   }

   public long getNbMethods() {
      return (long)this.methods.size();
   }

   public long getNbInvocations() {
      return this.invocations.keySet().stream().map((source) -> {
         return (Map)this.invocations.get(source);
      }).map((destination) -> {
         return destination.values();
      }).flatMap(Collection::stream).flatMapToLong((value) -> {
         return LongStream.of((long)value);
      }).sum();
   }

   public Map<String, Map<String, Integer>> getInvocations() {
      return this.invocations;
   }

   public boolean addMethod(String method) {
      return this.methods.add(method);
   }

   public boolean addMethods(Set<String> methods) {
      return methods.addAll(methods);
   }

   public void addInvocation(String source, String destination) {
      if (this.invocations.containsKey(source)) {
         if (((Map)this.invocations.get(source)).containsKey(destination)) {
            int numberOfArrows = (Integer)((Map)this.invocations.get(source)).get(destination);
            ((Map)this.invocations.get(source)).put(destination, numberOfArrows + 1);
         } else {
            this.methods.add(destination);
            ((Map)this.invocations.get(source)).put(destination, 1);
         }
      } else {
         this.methods.add(source);
         this.methods.add(destination);
         this.invocations.put(source, new HashMap());
         ((Map)this.invocations.get(source)).put(destination, 1);
      }

   }

   public void addInvocation(String source, String destination, int occurrences) {
      this.methods.add(source);
      this.methods.add(destination);
      if (!this.invocations.containsKey(source)) {
         this.invocations.put(source, new HashMap());
      }

      ((Map)this.invocations.get(source)).put(destination, occurrences);
   }

   public void addInvocations(Map<String, Map<String, Integer>> map) {
      Iterator var3 = map.keySet().iterator();

      while(var3.hasNext()) {
         String source = (String)var3.next();
         Iterator var5 = ((Map)map.get(source)).keySet().iterator();

         while(var5.hasNext()) {
            String destination = (String)var5.next();
            this.addInvocation(source, destination, (Integer)((Map)map.get(source)).get(destination));
         }
      }

   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Static Call Graph");
      builder.append("\nMethods: " + this.methods.size() + ".");
      builder.append("\nInvocations: " + this.getNbInvocations() + ".");
      builder.append("\n");
      Iterator var3 = this.invocations.keySet().iterator();

      while(var3.hasNext()) {
         String source = (String)var3.next();
         builder.append(source + ":\n");
         Iterator var5 = ((Map)this.invocations.get(source)).keySet().iterator();

         while(var5.hasNext()) {
            String destination = (String)var5.next();
            builder.append("\t---> " + destination + " (" + ((Map)this.invocations.get(source)).get(destination) + " time(s))\n");
         }

         builder.append("\n");
      }

      return builder.toString();
   }

   public void print() {
      this.loggerChain.log(new LogRequest(this.toString(), StandardLogRequestLevel.INFO));
   }

   public void log() {
      this.loggerChain.setFilePath(((EclipseJDTASTParser)this.parser).getProjectPath() + "static-callgraph.info");
      this.loggerChain.log(new LogRequest(this.toString(), StandardLogRequestLevel.DEBUG));
   }
} 