(ns gforcedev.cub3intime.components.timer 
  (:require [reagent.core :as r]
            [tailwind-hiccup.core :refer [tw]]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defn pad-to-2 [n]
  (let [s (str n)] (if (= (count s) 2) s (recur (str "0" s)))))

(defn format-time [n]
  (let [floored (js/Math.floor n)
        ms (mod n 1)
        s (mod floored 60)
        m (-> floored (- s) (mod 3600) (/ 60))
        h (-> floored (- s (* m 60)) (/ 3600))]
    (str (if (> h 0) (str h ":") "")
         (if (> h 0)
           (str (pad-to-2 m) ":")
           (if (> m 0) (str m ":") ""))
         (if (some #(> % 0) [h m])
           (pad-to-2 s) s)
         "." (-> ms (* 100) (js/Math.floor) (pad-to-2)))))

(def time-color-classes
  {:stopped :text-grey-400
   :ready :text-green-400
   :running :text-grey-400
   :stopping :text-red-400})

(defn update-timer-state! [state scramble-callback]
  (case (:timer-phase @state)
    :stopped (do
               (swap! state #(assoc % :current-time 0))
               (swap! state #(assoc % :timer-phase :ready)))
    :ready (swap! state #(assoc % :timer-phase :running))
    :running (swap! state #(assoc % :timer-phase :stopping))
    :stopping (do
                (swap! state #(assoc % :timer-phase :stopped))
                (scramble-callback))))

(defn inc-time! [state]
  (let [old-last-tick (@state :last-tick)]
    (swap! state #(assoc % :last-tick (js/Date.now)))
    (when (= (@state :timer-phase) :running)
      (swap! state #(assoc % :current-time
                           (+ (@state :current-time)
                              (-> (js/Date.now)
                                  (- old-last-tick)
                                  (/ 1000))))))))

(defn timer-component [timer-state scramble-callback!]
  (let [phase (r/cursor timer-state [:timer-phase])
        is-down (r/cursor timer-state [:is-down])]
    (defonce keydown-listener
      (events/listen js/window EventType.KEYDOWN
                     #(when (= (.-key %) " ")
                        (when (not @is-down)
                          (reset! is-down true)
                          (update-timer-state! timer-state scramble-callback!)))))
    (defonce keyup-listener
      (events/listen js/window EventType.KEYUP
                     #(when (= (.-key %) " ")
                        (reset! is-down false)
                        (update-timer-state! timer-state scramble-callback!))))
    (fn [timer-state]
      (js/setTimeout #(inc-time! timer-state) 10)
      [:div
       (tw [:text-7xl :text-center :p-20 (time-color-classes (@timer-state :timer-phase))])
       (format-time (@timer-state :current-time))])))

