(ns clojure.io.github.raefaldhia.cg_final.engine
    (:import [com.jogamp.newt.event KeyEvent]
             [com.jogamp.opengl GL2])
    (:require [clojure.io.github.raefaldhia.cg_final.keyboard :as Keyboard]
              [clojure.io.github.raefaldhia.cg_final.world :as World]
              [clojure.io.github.raefaldhia.cg_final.bot :as Bot]
              [clojure.io.github.raefaldhia.cg_final.player :as Player]))

(declare -create
         -create-dt
         -create-loop
         -get-timestamp
         -init
         -render
         -run
         -stop
         -update)

(defn -create
      [] 
      {:exit  (promise)
       :pitch (- 45)
       :world (World/-create)})

(defn- -create-dt
       []
       (let [start (-> (-get-timestamp)
                       (atom))]
            (fn []
                (let [end (-get-timestamp)
                      dt  (- end @start)]
                     (reset! start end)
                     dt))))

(defn- -create-loop
       [-display]
       (fn [engine events dt]
           (let [engine (-update engine events dt)]
                (-display (fn [gl glu]
                              (-render engine gl glu)))
                engine)))

(defn- -get-timestamp
       []
       (/ (System/nanoTime) 1000000.0))

(defn- -init
       [engine gl glu]
       (.glClearColor gl 0.0 0.0 0.0 1.0)
       (.glEnable gl GL2/GL_DEPTH_TEST)
       (-> engine 
           (update :world World/-init gl glu)
           (update :world
                   World/-put-object 
                   (-> (Bot/-create {:position [0.0 9.0 0.0]
                                     :rotation [0.0 0.0 180.0]})))
           (update :world
                   World/-put-object
                   (-> (Player/-create {:position [0.0 (- 9.0) 0.0]})))))

(defn- -render
       [engine gl glu]
       (.glClear gl (bit-or GL2/GL_COLOR_BUFFER_BIT GL2/GL_DEPTH_BUFFER_BIT))
       (.glLoadIdentity gl)
       (.glColor3f gl 1.0 1.0 1.0)
       (.glRotatef gl (:pitch engine) 1.0 0.0 0.0)
       (World/-render (:world engine) gl glu))

(defn -run
      [engine -init-fn -get-events]
      (let [[-loop engine] (let [new-engine (promise)]
                                (vector (-create-loop (-init-fn (fn [gl glu]
                                                                    (deliver new-engine
                                                                             (-init engine gl glu)))))
                                        (deref new-engine)))
            -dt            (-create-dt)]
           (loop [engine engine]
                 (let [dt     (-dt)
                       engine (-loop engine (-get-events) dt)]
                      (when-not (realized? (:exit engine))
                                (Thread/yield)
                                (recur engine))))))

(defn- -stop
       [engine]
       (deliver (:exit engine) nil)
       engine)

(defn- -update 
       [engine events dt]
       (let [keyboard-event (:keyboard events)]
            (-> engine 
                (cond-> (Keyboard/-get-state keyboard-event
                                             KeyEvent/VK_ESCAPE)    (-stop)
                        (Keyboard/-get-state keyboard-event
                                             KeyEvent/VK_PAGE_DOWN) (update-in [:pitch] + 1.0)
                        (Keyboard/-get-state keyboard-event
                                             KeyEvent/VK_PAGE_UP)   (update-in [:pitch] - 1.0))
                (update :world World/-update events dt))))