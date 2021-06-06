package com.example.rxjavajava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG  = "MainActivity";

    // ui
    private TextView text;

    // vars

    /**  Disposable
     *   使用している全てのObserverを追跡して、
     * 　不要になった場合にそれらをクリアする際に役立つもの
     * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        /** RxJava
         *  1.Observableオブジェクト（監視対象）を作成する
         *  2.Operatorで通知するデータの変換や選別を行う
         *  3.subscribeOnでどこで処理が行われるのかを定義する（バッグスレッド）
         *  4.observeOnでどこで通知されるのかを定義する（メインスレッド）
         * */

        // Observable : 生産者
        // Task型のObservableオブジェクトを作成
        // リストからObservableを作成
        Observable<Task> taskObservable = Observable

                // fromIterable : リストの中身を1つずつ取り出して、Observableに戻す
                // fromIterableはOperatorである
                // Operatorは通知するデータの変換や選別を行う
                .fromIterable(DataSource.createTasksList())

                // どこで（where）処理（worker thread)が行われるのかを特定する
                // subscribeされた時のスレッドを指定するため、何回も指定するものではない
                // SchedulersIOはAndroidRX特有のもの
                // subscribeは「書く、署名する」の意味
                .subscribeOn(Schedulers.io())

                // コメントアウト2
                // filterもoperatorである
                // 実行する場合は3をコメントアウトする
//                .filter(new Predicate<Task>() {
//                    @Override
//                    public boolean test(Task task) throws Throwable {
//                        Log.d(TAG, "test : " + Thread.currentThread().getName());
//                        return task.isComplete();
//                    }
//                })

                // コメントアウト3
                // 実行する場合は1, 2をコメントアウトする
                // task.isCompleteがtrueの場合にのみメインスレッドで実行される
                // RxCachedThreadScheduler-1はバッグスレッドの名称である

                .filter( new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Throwable {
                        Log.d(TAG, "test : " + Thread.currentThread().getName());

                        // バックグランドスレッドをストップさせる
                        // メインスレッドはストップさせない
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return task.isComplete();
                    }
                })

                // どのスレッドで処理の結果が監視されるのか
                // observe（観察）
                // それ以降の処理をどのスレッドで行うのかを指定する（長い処理の中で何回も指定される可能性がある）
                // AndroidSchedulersはAndroidRX特有のもの
                .observeOn(AndroidSchedulers.mainThread());

        // 新しいObservableオブジェクトを作成して、Observableに対してsubscribeする
        taskObservable.subscribe(new Observer<Task>() {
            @Override

            // ObservableがsubscribeされたタイミングでonSubscribeメソッドがすぐに呼び出される
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe : called.");
            }

            // iterate 繰り返し
            // Observableオブジェクトの中身を取り出して出力していく
            @Override
            public void onNext(@NonNull Task task) {

                // スレッドの名前を出力していく
                Log.d(TAG, "onNext : " + Thread.currentThread().getName());

                // Observableオブジェクトの中身を出力していく
                Log.d(TAG, "onNext : " + task.getDescription());

                // コメントアウト1
                // 繰り返しの処理を行う際に1000ミリ秒を待つ処理を加える
                // 出力はメインスレッドで行われているため、全ての処理が行われるまでUIがフリーズしてしまう
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            //エラーが発生したときに呼び出される
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError : " + e);
            }

            @Override

            // 処理が完了した際に呼び出される
            public void onComplete() {
                Log.d(TAG, "onComplete : called.");
            }
        });
    }
}