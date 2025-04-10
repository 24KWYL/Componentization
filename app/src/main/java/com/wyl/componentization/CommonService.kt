package com.wyl.componentization

import com.componentization.annotation.ServiceAnnotation

interface CommonService

@ServiceAnnotation
class CommonServiceImpl : CommonService