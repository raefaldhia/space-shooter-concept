(defproject cg-final "0.1.0-SNAPSHOT"
            :repositories [["jitpack" "https://jitpack.io"]]
            :dependencies [[org.clojure/clojure "1.10.1"]
                           [org.jogamp.gluegen/gluegen-rt-main "2.3.2"]
                           [org.jogamp.jogl/jogl-all-main "2.3.2"]
                           [de.javagl/obj "0.3.0"]]
            :main ^:skip-aot clojure.io.github.raefaldhia.cg_final.core
            :profiles {:uberjar {:aot :all}})