(ns gforcedev.cub3intime.components.timer 
  (:require [reagent.core :as r]
            [tailwind-hiccup.core :refer [tw]]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defn add-missing-decimal [s]
  (if (re-find #"\." s) s (str s ".00")))

(defn pad-to-2 [s]
  (if (= (count s) 2)
    s
    (recur (str "0" s))))

(defn format-time [n]
  (let [[_ raw-secs raw-millis] (->> n (str) (add-missing-decimal) (re-find #"(\d+)\.(\d\d)") (mapv js/parseInt))
        hours (if (>= raw-secs 3600) (-> 3661 (/ 3600) Math/floor) 0)
        mins (if (>= raw-secs 60) (-> (- raw-secs (* 3600 hours)) (/ 60) Math/floor) 0)
        secs (-> raw-secs (- (* hours 3600)) (- (* mins 60)) (str) (pad-to-2))
        millis (-> raw-millis (str) (pad-to-2))
        formatted-hours (if (> hours 0) (str hours ":") "")
        formatted-mins (if (or (> mins 0) (> hours 0)) (str (-> mins (str) ((if (> hours 0) pad-to-2 identity))) ":") "")]
    (str formatted-hours formatted-mins secs "." millis)))

(def time-color-classes
  {:stopped :text-grey-400
   :ready :text-green-400
   :running :text-grey-400
   :stopping :text-red-400})

(def timer-state-updaters!
  {:stopped (fn [state]
              (swap! state #(assoc % :current-time 0))
              (swap! state #(assoc % :timer-phase :ready)))
   :ready (fn [state]
            (swap! state #(assoc % :timer-phase :running)))
   :running (fn [state]
              (swap! state #(assoc % :timer-phase :stopping)))
   :stopping (fn [state]
               (swap! state #(assoc % :timer-phase :stopped)))})

(defn inc-time! [state]
  (let [old-last-tick (@state :last-tick)]
    (swap! state #(assoc % :last-tick (js/Date.now)))
    (when (= (@state :timer-phase) :running)
      (swap! state #(assoc % :current-time
                           (+ (@state :current-time) (-> (js/Date.now) (- old-last-tick) (/ 1000))))))))

(defonce app-state (r/atom {:current-time 0
                            :timer-phase :stopped
                            :is-down false}
                           :last-tick 0))

(def phase (r/cursor app-state [:timer-phase]))
(def is-down (r/cursor app-state [:is-down]))

(defonce keydown-listener (events/listen js/window EventType.KEYDOWN
                                         #(when (= (.-key %) " ")
                                            (when (not @is-down)
                                              (reset! is-down true)
                                              ((timer-state-updaters! @phase) app-state)))))

(defonce keyup-listener (events/listen js/window EventType.KEYUP
                                       #(when (= (.-key %) " ")
                                            (reset! is-down false)
                                            ((timer-state-updaters! @phase) app-state))))

(defn timer-component []
  (js/setTimeout #(inc-time! app-state) 10)
  [:div
   (tw [:text-7xl :text-center :p-20 (time-color-classes (@app-state :timer-phase))])
   (format-time (@app-state :current-time))])

