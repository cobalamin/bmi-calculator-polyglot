(ns bmi-reagent.app
  (:require [reagent.core :as r]))

(defonce bmi-data (r/atom {:height 180 :weight 80}))

(defn calc-bmi [height weight]
  "Calculate the body mass index for the given height and weight."
  (let [h (/ height 100)]
    (/ weight (* h h))))

(defn slider [param value min max & [recalc-with]]
  [:input {:type "range" :value value :min min :max max
           :style {:width "100%"}
           :on-change (fn [e]
                        (if (= param :bmi)
                          (let [bmi (.-target.value e)
                                h (/ recalc-with 100)
                                weight (* bmi (* h h))]
                            (swap! bmi-data assoc :weight weight))
                          (swap! bmi-data assoc param (.-target.value e))))}])

(defn bmi-component []
  (let [{:keys [height weight]} @bmi-data
        bmi (calc-bmi height weight)
        [color diagnostic] (cond
                             (< bmi 18.5) ["orange" "underweight"]
                             (< bmi 25) ["inherit" "normal"]
                             (< bmi 30) ["orange" "overweight"]
                             :else ["red" "obese"])]
    [:div
     [:div
      "Height: " (int height) "cm"
      [slider :height height 100 220]]
     [:div
      "Weight: " (int weight) "kg"
      [slider :weight weight 30 150]]
     [:div
      "BMI: " (int bmi) " "
      [:span {:style {:color color}} diagnostic]
      [slider :bmi bmi 10 50 height]]]))

(defn init []
  (r/render-component [bmi-component]
                      (.getElementById js/document "app")))
