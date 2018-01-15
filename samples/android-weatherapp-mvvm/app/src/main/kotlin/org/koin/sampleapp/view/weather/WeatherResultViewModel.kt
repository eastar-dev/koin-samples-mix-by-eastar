package org.koin.sampleapp.view.weather

import android.arch.lifecycle.MutableLiveData
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.getDailyForecasts
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.sampleapp.view.AbstractViewModel
import org.koin.sampleapp.view.SingleLiveEvent

/**
 * Weather Presenter
 */
class WeatherResultViewModel(private val weatherRepository: WeatherRepository, private val scheduler: SchedulerProvider) : AbstractViewModel() {

    val weatherList = MutableLiveData<WeatherResultUIModel>()
    val selectEvent = SingleLiveEvent<SelectEvent>()

    init {
        getWeatherList()
    }

    fun getWeatherList() {
        launch {
            weatherRepository.getWeather().map { it.getDailyForecasts() }.with(scheduler)
                    .subscribe({ list ->
                        weatherList.value = WeatherResultUIModel(list)
                    }, { e ->
                        weatherList.value = WeatherResultUIModel(error = e)
                    })
        }
    }

    fun selectWeatherDetail(detail: DailyForecastModel) {
        launch {
            weatherRepository.selectWeatherDetail(detail).with(scheduler).subscribe {
                selectEvent.value = SelectEvent(isSelected = true)
            }
        }
    }
}

data class WeatherResultUIModel(val list: List<DailyForecastModel> = emptyList(), val error: Throwable? = null)
data class SelectEvent(val isSelected: Boolean = false, val error: Throwable? = null)