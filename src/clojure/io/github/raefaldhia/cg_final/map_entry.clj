(ns clojure.io.github.raefaldhia.cg_final.map_entry)

(defn map-entry
      [k v]
      (clojure.lang.MapEntry. k v))

(defn assocme
      [m k & args]
      (map-entry (key m)
                 (apply (partial assoc (val m) k) args)))

(defn assocme-in
      [m ks & args]
      (map-entry (key m)
                 (apply (partial assoc-in (val m) ks) args)))

(defn getme
      [m & args]
      (apply (partial get (val m)) args))

(defn getme-in
      [m & args]
      (apply (partial get-in (val m)) args))

(defn updateme
      [m k f & args]
      (map-entry (key m)
                 (apply (partial update (val m) k f) args)))

(defn updateme-in
      [m ks f & args]
      (map-entry (key m)
                 (apply (partial update-in (val m) ks f) args)))