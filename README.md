# android_flowLayout

/**
 * Created by hanyh on 2017/10/17.
 * 自定义流式布局
 * onMeasure方法中 如果布局的宽设置的是wrap_content那么getMode得到的是at_most但是会反复测量，
 * 先测量at_most得到目标的值，最后在exactly一次 只不过这个得到的值是at_most时算出的值，这也就是系统反复测量的原因
 * 高度的测量正好相反 如果布局设置的是wrap_content那么getMode得到的是at_most，如果布局设置的是match_parent
 * 那么getMode得到的是exactly 但是会反复测量 先测量at_most得到目标的值，最后在exactly一次 只不过这个得到的值是at_most时算出的值
 * 补充：高的测量是根据宽的测量模式而定：
 * 比如：宽是match_parent 高是 match_parent 那么测量过程是-》
 * 宽：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 * 高：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 *
 * 宽是match_parent 高是 wrap_content 那么测量过程是——》
 * 宽：EXACTLY——》EXACTLY——》EXACTLY——》EXACTLY
 * 高：EXACTLY——》AT_MOST——》EXACTLY——》AT_MOST
 *
 * 宽是wrap_content 高是 match_parent 那么测量过程是——》
 * 宽：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 * 高：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 *
 * 宽是wrap_content 高是 wrap_content 那么测量过程是——》
 * 宽：AT_MOST——》EXACTLY——》AT_MOST——》EXACTLY
 * 高：AT_MOST——》AT_MOST——》AT_MOST——》AT_MOST
 *
 * 注意如果不super.onMeasure(widthMeasureSpec,heightMeasureSpec)，不调用setMeasuredDimension方法会报错
 * MeasureSpec.getSize(widthMeasureSpec);如果不测量默认就是屏幕的宽和高
 */
