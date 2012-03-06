/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ioc.rebind.ioc.builtin;

import org.jboss.errai.codegen.framework.Context;
import org.jboss.errai.codegen.framework.Statement;
import org.jboss.errai.codegen.framework.meta.MetaMethod;
import org.jboss.errai.codegen.framework.meta.MetaParameter;
import org.jboss.errai.codegen.framework.util.Stmt;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.InjectableInstance;

/**
 * @author Mike Brock
 */
@CodeDecorator
public class AfterInitializationExtension extends IOCDecoratorExtension<AfterInitialization> {
  public AfterInitializationExtension(Class<AfterInitialization> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public Statement generateDecorator(InjectableInstance<AfterInitialization> instance) {
    final Context ctx = instance.getInjectionContext().getProcessingContext().getContext();
    final MetaMethod method = instance.getMethod();

    if (!method.isPublic()) {
      instance.ensureMemberExposed();
    }

    Statement callbackStmt = Stmt.newObject(Runnable.class).extend()
            .publicOverridesMethod("run")
            .append(instance.callOrBind())
            .finish()
            .finish();

    return Stmt.create(ctx).invokeStatic(InitVotes.class, "registerOneTimeInitCallback", callbackStmt);
  }
}
